package top.ilov.mcmods.tc.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

public class NetherTeleportHelper {

    @Nullable
    public static BlockPos findSafeNetherSpawn(ServerLevel nether, Player player) {
        BlockPos center = new BlockPos(0, 70, 0);
        int radius = 16;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                for (int dy = 64; dy < 120; dy++) {
                    BlockPos candidate = new BlockPos(pos.getX(), dy, pos.getZ());
                    if (isSafeSpawn(nether, candidate, player)) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }

    public static boolean isSafeSpawn(ServerLevel world, BlockPos pos, Player player) {
        BlockState state = world.getBlockState(pos);
        BlockState above = world.getBlockState(pos.above());
        BlockState below = world.getBlockState(pos.below());

        return state.isAir() && above.isAir() && below.isCollisionShapeFullBlock(world, pos.below());
    }

    public static boolean hasExistingOverworldCake(ServerLevel world, BlockPos center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (world.getBlockState(check).is(BlocksRegistry.overworld_cake.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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
        BlockPos cakePos = spawnPos.relative(facing);
        BlockPos groundCenter = cakePos.below();

        if (hasExistingOverworldCake(world, cakePos, 3)) {
            return;
        }

        boolean needPlatform = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = groundCenter.offset(dx, 0, dz);
                if (world.isEmptyBlock(check)) {
                    needPlatform = true;
                    break;
                }
            }
            if (needPlatform) break;
        }

        if (needPlatform) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = groundCenter.offset(dx, 0, dz);
                    world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 1; dy <= 3; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = groundCenter.offset(dx, dy, dz);
                    if (!world.isEmptyBlock(pos)) {
                        world.destroyBlock(pos, false);
                    }
                }
            }
        }

        if (world.isEmptyBlock(cakePos)) {
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
    private static BlockPos findTorchPosition(ServerLevel world, BlockPos facing) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = facing.relative(dir);
            BlockPos below = candidate.below();
            if (world.isEmptyBlock(candidate) && world.getBlockState(below).isCollisionShapeFullBlock(world, below)) {
                return candidate;
            }
        }
        return null;
    }

}
