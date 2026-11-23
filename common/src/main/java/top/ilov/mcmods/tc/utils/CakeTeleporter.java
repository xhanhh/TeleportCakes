package top.ilov.mcmods.tc.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.mixin.PlayerRespawnLogicInvoker;

import java.util.Optional;

@UtilityClass
public final class CakeTeleporter {

    public static boolean teleportToOverworld(ServerPlayer player) {

        if (player.isRemoved()) {
            return false;
        }

        BlockPos respawnPos = player.getRespawnPosition();
        ServerLevel respawnLevel = player.serverLevel().getServer().getLevel(player.getRespawnDimension());
        float respawnAngle = player.getRespawnAngle();

        if (respawnPos != null && respawnLevel != null) {

            Optional<Vec3> standUpPos = Player.findRespawnPositionAndUseSpawnBlock(
                    respawnLevel, respawnPos, respawnAngle, player.isRespawnForced(), false);

            if (standUpPos.isPresent()) {

                Vec3 target = standUpPos.get();
                Block spawnBlock = respawnLevel.getBlockState(respawnPos).getBlock();

                if (spawnBlock instanceof BedBlock || spawnBlock instanceof RespawnAnchorBlock) {
                    Vec3 facing = Vec3.atBottomCenterOf(respawnPos).subtract(target).normalize();
                    respawnAngle = (float) Mth.wrapDegrees(Mth.atan2(facing.z, facing.x) * Mth.RAD_TO_DEG - 90.0);
                }

                return teleportToRespawn(player, respawnLevel, target, respawnAngle);
            }
            player.connection.send(new ClientboundGameEventPacket(
                    ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        ServerLevel overworld = player.serverLevel().getServer().overworld();
        Vec3 target = Vec3.atBottomCenterOf(findSharedSpawn(overworld, player));

        return teleportToRespawn(player, overworld, target, 0.0F);

    }

    private static boolean teleportToRespawn(ServerPlayer player, ServerLevel level, Vec3 target, float yaw) {

        boolean changesDimension = level != player.serverLevel();
        player.teleportTo(level, target.x, target.y, target.z, yaw, 0.0F);
        if (player.serverLevel() != level) {
            return false;
        }
        if (changesDimension) {
            player.setDeltaMovement(Vec3.ZERO);
        }
        return true;
    }

    private static BlockPos findSharedSpawn(ServerLevel level, ServerPlayer player) {

        BlockPos spawn = level.getSharedSpawnPos();
        AABB standingBox = player.getDimensions(Pose.STANDING).makeBoundingBox(Vec3.ZERO);

        if (level.dimensionType().hasSkyLight()
                && level.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {

            int borderDistance = Mth.floor(level.getWorldBorder().getDistanceToBorder(spawn.getX(), spawn.getZ()));
            int radius = borderDistance <= 1 ? 1 : Math.min(borderDistance, Math.max(0, level.getServer().getSpawnRadius(level)));
            long diameter = 2L * radius + 1;
            int columns = (int) Math.min(diameter * diameter, Integer.MAX_VALUE);
            int step = columns <= 16 ? columns - 1 : 17;
            int first = RandomSource.create().nextInt(columns);

            for (int i = 0; i < columns; i++) {
                long index = (first + (long) step * i) % columns;
                int x = spawn.getX() + (int) (index % diameter) - radius;
                int z = spawn.getZ() + (int) (index / diameter) - radius;
                BlockPos candidate = PlayerRespawnLogicInvoker.teleportcakes$getOverworldRespawnPos(level, x, z);
                if (candidate != null && level.noCollision(player, standingBox.move(Vec3.atBottomCenterOf(candidate)))) {
                    return candidate;
                }
            }
        }

        while (spawn.getY() < level.getMaxBuildHeight() - 1
                && !level.noCollision(player, standingBox.move(Vec3.atBottomCenterOf(spawn)))) {
            spawn = spawn.above();
        }

        while (spawn.getY() > level.getMinBuildHeight() + 1
                && level.noCollision(player, standingBox.move(Vec3.atBottomCenterOf(spawn.below())))) {
            spawn = spawn.below();
        }
        return spawn;
    }

    public static boolean teleportToNether(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel nether = sourceLevel.getServer().getLevel(Level.NETHER);
        if (nether == null) {
            return false;
        }

        BlockPos spawnPos = NetherTeleportHelper.getOrCreateNetherSpawn(nether, player, player.getDirection());
        Vec3 targetPos = spawnPos.getCenter();
        player.teleportTo(nether, targetPos.x, targetPos.y, targetPos.z, player.getYRot(), player.getXRot());
        return player.serverLevel() == nether;

    }

    public static boolean teleportToEnd(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel endLevel = sourceLevel.getServer().getLevel(Level.END);
        if (endLevel == null) {
            return false;
        }

        BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;
        Vec3 targetPos = spawnPos.getCenter();
        createEndPlatform(endLevel, spawnPos.below(2));

        player.teleportTo(endLevel, targetPos.x, targetPos.y, targetPos.z, Direction.WEST.toYRot(), player.getXRot());
        if (player.serverLevel() != endLevel) {
            return false;
        }
        player.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        placeOverworldCakeNearEndSpawn(endLevel, spawnPos);
        return true;

    }

    private static void createEndPlatform(ServerLevel level, BlockPos floor) {

        for (BlockPos pos : BlockPos.betweenClosed(floor.offset(-2, 0, -2), floor.offset(2, 3, 2))) {
            Block expected = pos.getY() == floor.getY() ? Blocks.OBSIDIAN : Blocks.AIR;
            if (!level.getBlockState(pos).is(expected)) {
                level.destroyBlock(pos, true, null);
                level.setBlock(pos, expected.defaultBlockState(), 3);
            }
        }

    }

    private static void placeOverworldCakeNearEndSpawn(ServerLevel level, BlockPos spawnPos) {

        BlockPos cakePos = spawnPos.relative(Direction.WEST);
        while (level.isEmptyBlock(cakePos.below())) {
            cakePos = cakePos.below();
        }

        if (level.isEmptyBlock(cakePos)) {
            level.setBlock(cakePos, BlocksRegistry.overworld_cake.get().defaultBlockState(), 3);
        }

    }

}