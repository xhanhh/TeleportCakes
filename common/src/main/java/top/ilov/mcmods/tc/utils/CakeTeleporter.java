package top.ilov.mcmods.tc.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

@UtilityClass
public final class CakeTeleporter {

    public static boolean teleportToOverworld(ServerPlayer player) {

        DimensionTransition dimensionTransition = player.findRespawnPositionAndUseSpawnBlock(
                false, DimensionTransition.DO_NOTHING
        );
        return player.changeDimension(dimensionTransition) != null;

    }

    public static boolean teleportToNether(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel nether = sourceLevel.getServer().getLevel(Level.NETHER);
        if (nether == null) {
            return false;
        }

        BlockPos spawnPos = NetherTeleportHelper.getOrCreateNetherSpawn(nether, player, player.getDirection());
        Vec3 targetPos = spawnPos.getCenter();
        player.teleportTo(nether, targetPos.x, targetPos.y, targetPos.z, player.getYRot(), player.getXRot());
        return true;

    }

    public static boolean teleportToEnd(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel endLevel = sourceLevel.getServer().getLevel(Level.END);
        if (endLevel == null) {
            return false;
        }

        BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;
        Vec3 targetPos = spawnPos.getCenter();
        EndPlatformFeature.createEndPlatform(endLevel, spawnPos.below(), true);

        player.teleportTo(endLevel, targetPos.x, targetPos.y, targetPos.z, Direction.WEST.toYRot(), player.getXRot());
        player.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        placeOverworldCakeNearEndSpawn(endLevel, spawnPos);
        return true;

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