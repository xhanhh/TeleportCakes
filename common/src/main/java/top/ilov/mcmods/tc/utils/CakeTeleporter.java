package top.ilov.mcmods.tc.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

@UtilityClass
public class CakeTeleporter {

    public static boolean teleportToOverworld(ServerPlayer player) {

        TeleportTransition teleportTransition = player.findRespawnPositionAndUseSpawnBlock(
                false, TeleportTransition.DO_NOTHING
        );

        return player.teleport(teleportTransition) != null;

    }

    public static boolean teleportToNether(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel nether = sourceLevel.getServer().getLevel(Level.NETHER);
        if (nether == null) {
            return false;
        }

        BlockPos spawnPos = NetherTeleportHelper.getOrCreateNetherSpawn(nether, player, player.getDirection());
        Vec3 targetPos = spawnPos.getCenter();

        return player.teleport(new TeleportTransition(
                nether,
                targetPos,
                player.getKnownMovement(),
                player.getYRot(),
                player.getXRot(),
                TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
        )) != null;

    }

    public static boolean teleportToEnd(ServerLevel sourceLevel, ServerPlayer player) {

        ServerLevel endLevel = sourceLevel.getServer().getLevel(Level.END);
        if (endLevel == null) {
            return false;
        }

        BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;
        EndPlatformFeature.createEndPlatform(endLevel, spawnPos.below(), true);

        Entity teleportedPlayer = player.teleport(new TeleportTransition(
                endLevel,
                spawnPos.getCenter(),
                player.getKnownMovement(),
                Direction.WEST.toYRot(),
                player.getXRot(),
                TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
        ));

        if (teleportedPlayer == null) {
            return false;
        }

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
