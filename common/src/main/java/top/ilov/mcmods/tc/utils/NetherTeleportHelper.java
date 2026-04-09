package top.ilov.mcmods.tc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NetherTeleportHelper {

    private static final int SEARCH_MAX_RADIUS = 96;
    private static final int SEARCH_MIN_Y = 32;
    private static final int SEARCH_MAX_Y = 120;

    private static final String PLAYER_TAG_SPAWN_POS_PREFIX = "teleportcakes_nether_spawn_pos=";

    // 保存世界的下届传送点，确保每个玩家都传到一样的地方
    private static final String WORLD_SPAWN_FILE_NAME = "teleportcakes-nether-spawn.json";
    private static final Gson WORLD_SPAWN_GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static boolean isLava(ServerLevel world, BlockPos pos) {
        return world.getFluidState(pos).is(FluidTags.LAVA);
    }

    private static boolean isFlowingLava(ServerLevel world, BlockPos pos) {
        return isLava(world, pos) && !world.getFluidState(pos).isSource();
    }

    private static boolean isSolidFloorBlock(ServerLevel world, BlockPos pos) {
        if (isLava(world, pos)) return false;
        return world.getBlockState(pos).isCollisionShapeFullBlock(world, pos);
    }

    // 生成 需要下方16/25格的方块为实体方块
    private static boolean hasEnoughFloor(ServerLevel world, BlockPos feetPos) {
        BlockPos floorCenter = feetPos.below();
        int solid = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (isSolidFloorBlock(world, floorCenter.offset(dx, 0, dz))) {
                    solid++;
                }
            }
        }
        return solid >= 16;
    }

    // 检查别生成在柱子上
    private static boolean hasTerrainConnection(ServerLevel world, BlockPos feetPos) {

        BlockPos floorCenter = feetPos.below();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.max(Math.abs(dx), Math.abs(dz)) != 3) continue;
                if (isSolidFloorBlock(world, floorCenter.offset(dx, 0, dz))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasSavedNetherSpawn(ServerPlayer player) {
        return getSavedNetherSpawn(player) != null;
    }

    @Nullable
    public static BlockPos getSavedNetherSpawn(ServerPlayer player) {

        for (String tag : player.entityTags()) {
            if (!tag.startsWith(PLAYER_TAG_SPAWN_POS_PREFIX)) continue;

            String payload = tag.substring(PLAYER_TAG_SPAWN_POS_PREFIX.length());
            String[] parts = payload.split(",", -1);
            if (parts.length != 3) return null;

            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                return new BlockPos(x, y, z);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private static void setSavedNetherSpawn(ServerPlayer player, BlockPos pos) {
        String toRemove = null;
        for (String tag : player.entityTags()) {
            if (tag.startsWith(PLAYER_TAG_SPAWN_POS_PREFIX)) {
                toRemove = tag;
                break;
            }
        }
        if (toRemove != null) {
            player.removeTag(toRemove);
        }
        player.addTag(PLAYER_TAG_SPAWN_POS_PREFIX + pos.getX() + "," + pos.getY() + "," + pos.getZ());
    }

    public static boolean hasWorldNetherSpawn(ServerLevel nether) {
        return getWorldNetherSpawn(nether) != null;
    }

    @Nullable
    public static BlockPos getWorldNetherSpawn(ServerLevel nether) {

        Path file = getWorldSpawnFile(nether);
        if (!Files.exists(file)) return null;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json == null) return null;
            if (!json.has("x") || !json.has("y") || !json.has("z")) return null;

            BlockPos pos = new BlockPos(json.get("x").getAsInt(),
                    json.get("y").getAsInt(), json.get("z").getAsInt());

            return isWithinBuildHeight(nether, pos) ? pos : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void setWorldNetherSpawn(ServerLevel nether, BlockPos pos) {

        Path file = getWorldSpawnFile(nether);

        try {
            Files.createDirectories(file.getParent());

            JsonObject json = new JsonObject();
            json.addProperty("x", pos.getX());
            json.addProperty("y", pos.getY());
            json.addProperty("z", pos.getZ());

            Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                writer.write(WORLD_SPAWN_GSON.toJson(json));
            }

            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {

        }
    }

    @NonNull
    private static Path getWorldSpawnFile(ServerLevel nether) {

        Path root = nether.getServer().getWorldPath(LevelResource.ROOT);
        return root.resolve("data").resolve(WORLD_SPAWN_FILE_NAME);
    }

    private static boolean isWithinBuildHeight(ServerLevel nether, BlockPos spawnPos) {
        int minY = nether.getMinY() + 1;
        int maxY = nether.getMaxY() - 2;
        return spawnPos.getY() >= minY && spawnPos.getY() <= maxY;
    }

    @NonNull
    public static BlockPos getOrCreateNetherSpawn(ServerLevel nether, ServerPlayer player, Direction facing) {

        BlockPos worldSaved = getWorldNetherSpawn(nether);
        if (worldSaved != null) {
            checkPlatformAndPlaceCake(nether, worldSaved, facing);
            setSavedNetherSpawn(player, worldSaved);
            return worldSaved;
        }

        BlockPos playerSaved = getSavedNetherSpawn(player);
        if (playerSaved != null && isWithinBuildHeight(nether, playerSaved)) {
            checkPlatformAndPlaceCake(nether, playerSaved, facing);
            setWorldNetherSpawn(nether, playerSaved);
            return playerSaved;
        }

        BlockPos adopted = tryAdoptExistingSpawn(nether, player);
        if (adopted != null) {
            checkPlatformAndPlaceCake(nether, adopted, facing);
            setSavedNetherSpawn(player, adopted);
            setWorldNetherSpawn(nether, adopted);
            return adopted;
        }

        BlockPos spawnPos = findSafeNetherSpawn(nether, player);
        if (spawnPos == null) {
            spawnPos = findSafeNetherSpawnNear(nether, new BlockPos(0, 70, 0));
            if (spawnPos == null) {
                spawnPos = new BlockPos(0, 70, 0);
            }
        }

        checkPlatformAndPlaceCake(nether, spawnPos, facing);
        setSavedNetherSpawn(player, spawnPos);
        setWorldNetherSpawn(nether, spawnPos);
        return spawnPos;
    }

    private static boolean isLargeEmptyVolume(ServerLevel world, BlockPos feetPos) {

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos p = feetPos.offset(dx, dy, dz);
                    if (!world.isEmptyBlock(p)) return false;
                    if (isLava(world, p)) return false;
                }
            }
        }
        return true;
    }

    private static boolean hasLavaNearby(ServerLevel world, BlockPos center) {

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = 0; dy <= 12; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (isLava(world, center.offset(dx, dy, dz))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 以比例获取中心
    private static BlockPos getSearchCenter(Player player) {

        double scale = player.level().dimension().equals(Level.OVERWORLD) ? 8.0 : 1.0;
        int x = Mth.floor(player.getX() / scale);
        int z = Mth.floor(player.getZ() / scale);
        return new BlockPos(x, 70, z);
    }

    // 如果有平台就返回已有平台位置
    @Nullable
    private static BlockPos tryAdoptExistingSpawn(ServerLevel nether, Player player) {

        BlockPos center = getSearchCenter(player);

        int minY = Math.max(nether.getMinY() + 2, SEARCH_MIN_Y);
        int maxY = Math.min(nether.getMaxY() - 2, SEARCH_MAX_Y);
        if (maxY <= minY) {
            minY = nether.getMinY() + 2;
            maxY = nether.getMaxY() - 2;
        }

        int radius = 32;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                for (int y = maxY; y >= minY; y--) {
                    BlockPos cakePos = new BlockPos(x, y, z);
                    if (!nether.getBlockState(cakePos).is(BlocksRegistry.overworld_cake.get())) continue;

                    BlockPos spawn = findSpawnAdjacentToCake(nether, cakePos);
                    if (spawn != null) return spawn;
                }
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos findSpawnAdjacentToCake(ServerLevel world, BlockPos cakePos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = cakePos.relative(dir);
            if (!world.isEmptyBlock(candidate) || !world.isEmptyBlock(candidate.above())) continue;
            if (!isSolidFloorBlock(world, candidate.below())) continue;
            return candidate;
        }
        return null;
    }

    // 给岩浆拿空气填了
    private static void sealAgainstLava(ServerLevel world, BlockPos groundCenter) {

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy = 0; dy <= 12; dy++) {
                    BlockPos p = groundCenter.offset(dx, dy, dz);
                    boolean inCore = Math.abs(dx) <= 2 && Math.abs(dz) <= 2 && dy <= 4;
                    if (inCore && isLava(world, p)) {
                        world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    } else if (!inCore && isFlowingLava(world, p)) {
                        world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Nullable
    public static BlockPos findSafeNetherSpawn(ServerLevel nether, Player player) {
        return findSafeNetherSpawnNear(nether, getSearchCenter(player));
    }

    @Nullable
    private static BlockPos findSafeNetherSpawnNear(ServerLevel nether, BlockPos center) {
        int minY = Math.max(nether.getMinY() + 2, SEARCH_MIN_Y);
        int maxY = Math.min(nether.getMaxY() - 10, SEARCH_MAX_Y);
        if (maxY <= minY) {
            minY = nether.getMinY() + 2;
            maxY = nether.getMaxY() - 10;
        }

        for (int radius = 0; radius <= SEARCH_MAX_RADIUS; radius++) {
            int step = radius <= 24 ? 1 : 2;

            for (int dx = -radius; dx <= radius; dx += step) {

                BlockPos found = tryFindInColumn(nether, center.getX() + dx,
                        center.getZ() - radius, minY, maxY);
                if (found != null) return found;

                if (radius != 0) {
                    found = tryFindInColumn(nether, center.getX() + dx,
                            center.getZ() + radius, minY, maxY);
                    if (found != null) return found;
                }
            }

            for (int dz = -radius + step; dz <= radius - step; dz += step) {

                BlockPos found = tryFindInColumn(nether, center.getX() - radius,
                        center.getZ() + dz, minY, maxY);
                if (found != null) return found;

                found = tryFindInColumn(nether, center.getX() + radius,
                        center.getZ() + dz, minY, maxY);
                if (found != null) return found;
            }
        }

        int fallbackRadius = 16;
        for (int dx = -fallbackRadius; dx <= fallbackRadius; dx++) {
            for (int dz = -fallbackRadius; dz <= fallbackRadius; dz++) {
                BlockPos found = tryFindSmallInColumn(nether, center.getX() + dx, center.getZ() + dz, minY, maxY);
                if (found != null) return found;
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos tryFindInColumn(ServerLevel world, int x, int z, int minY, int maxY) {
        for (int y = maxY; y >= minY; y--) {
            BlockPos candidate = new BlockPos(x, y, z);
            if (!world.isEmptyBlock(candidate) || !world.isEmptyBlock(candidate.above())) continue;
            if (!hasEnoughFloor(world, candidate)) continue;
            if (!hasTerrainConnection(world, candidate)) continue;
            if (!isLargeEmptyVolume(world, candidate)) continue;
            if (hasLavaNearby(world, candidate.below())) continue;
            return candidate;
        }
        return null;
    }

    @Nullable
    private static BlockPos tryFindSmallInColumn(ServerLevel world, int x, int z, int minY, int maxY) {
        for (int y = maxY; y >= minY; y--) {
            BlockPos candidate = new BlockPos(x, y, z);
            if (!world.isEmptyBlock(candidate) || !world.isEmptyBlock(candidate.above())) continue;
            if (!isSolidFloorBlock(world, candidate.below())) continue;
            if (hasLavaNearby(world, candidate.below())) continue;
            return candidate;
        }
        return null;
    }

    // 选取最近的蛋糕
    @Nullable
    private static BlockPos findExistingOverworldCakeNear(ServerLevel world, BlockPos spawnPos) {

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = spawnPos.relative(dir);
            if (world.getBlockState(candidate).is(BlocksRegistry.overworld_cake.get())) {
                return candidate;
            }
        }

        int radius = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos candidate = spawnPos.offset(dx, 0, dz);
                if (world.getBlockState(candidate).is(BlocksRegistry.overworld_cake.get())) {
                    return candidate;
                }
            }
        }
        return null;
    }

//    public static boolean hasExistingOverworldCake(ServerLevel world, BlockPos center, int radius) {
//        for (int dx = -radius; dx <= radius; dx++) {
//            for (int dy = -1; dy <= 2; dy++) {
//                for (int dz = -radius; dz <= radius; dz++) {
//                    BlockPos check = center.offset(dx, dy, dz);
//                    if (world.getBlockState(check).is(BlocksRegistry.overworld_cake.get())) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    public static boolean hasExistingTorch(ServerLevel world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (world.getBlockState(check).is(Blocks.TORCH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void checkPlatformAndPlaceCake(ServerLevel world, BlockPos spawnPos, Direction facing) {
        BlockPos groundCenter = spawnPos.below();

        BlockPos existingCake = findExistingOverworldCakeNear(world, spawnPos);
        BlockPos cakePos = existingCake != null ? existingCake : spawnPos.relative(facing);

        boolean lavaNearby = hasLavaNearby(world, groundCenter);
        boolean needPlatform = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = groundCenter.offset(dx, 0, dz);
                if (world.isEmptyBlock(check) || isLava(world, check)) {
                    needPlatform = true;
                    break;
                }
            }
            if (needPlatform) break;
        }

        if (needPlatform) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = groundCenter.offset(dx, 0, dz);
                    world.setBlock(p, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }

        // 清空上方方块如果有方块
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos p = spawnPos.above(dy);
            if (isLava(world, p)) {
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            } else if (!world.isEmptyBlock(p)) {
                world.destroyBlock(p, false);
            }
        }

        // 清空平台上方2 3 4格 3x3空间的方块
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 2; dy <= 4; dy++) {
                    BlockPos p = groundCenter.offset(dx, dy, dz);
                    if (isLava(world, p)) {
                        world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    } else if (!world.isEmptyBlock(p)) {
                        world.destroyBlock(p, false);
                    }
                }
            }
        }

        if (lavaNearby) {
            sealAgainstLava(world, groundCenter);
        }

        // 放主世界蛋糕
        if (!world.getBlockState(cakePos).is(BlocksRegistry.overworld_cake.get())) {
            if (!world.isEmptyBlock(cakePos)) {
                world.destroyBlock(cakePos, false);
            }
            world.setBlock(cakePos, BlocksRegistry.overworld_cake.get().defaultBlockState(), 3);
        }

        if (!hasExistingTorch(world, cakePos)) {
            BlockPos torchPos = findTorchPosition(world, cakePos);
            if (torchPos != null) {
                world.setBlock(torchPos, Blocks.TORCH.defaultBlockState(), 3);
            }
        }
    }

    @Nullable
    private static BlockPos findTorchPosition(ServerLevel world, BlockPos cakePos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = cakePos.relative(dir);
            BlockPos below = candidate.below();
            if (world.isEmptyBlock(candidate) && world.getBlockState(below).isCollisionShapeFullBlock(world, below)) {
                return candidate;
            }
        }
        return null;
    }
}
