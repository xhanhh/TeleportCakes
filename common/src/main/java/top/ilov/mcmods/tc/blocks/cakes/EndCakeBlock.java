package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

public class EndCakeBlock extends CakeTeleportBase {

    public EndCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                           Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.ENDER_EYE && state.getValue(BITES) > 0 && !level.isClientSide()) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                            @NotNull Player player, @NotNull BlockHitResult hitResult) {

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel
                && !player.isSpectator() && !player.isPassenger()) {

            if (serverLevel.dimension().equals(Level.END)) {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_end_cake"), true);
                return InteractionResult.PASS;
            }

            ServerLevel targetLevel = serverLevel.getServer().getLevel(Level.END);
            if (targetLevel == null) {
                return InteractionResult.FAIL;
            }

            BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;
            Vec3 targetPos = spawnPos.getCenter();

            EndPlatformFeature.createEndPlatform(targetLevel, spawnPos.below(), true);

            float yaw = Direction.WEST.toYRot();

            Player teleportedPlayer = (Player) player.teleport(
                    new TeleportTransition(targetLevel, targetPos, player.getKnownMovement(), yaw, player.getXRot(),
                            TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)));

            if (teleportedPlayer == null) {
                return InteractionResult.FAIL;
            }

            placeOverworldCakeNearEndSpawn(targetLevel, spawnPos, player);

            return eat(level, pos, state, player);
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private static void placeOverworldCakeNearEndSpawn(ServerLevel level, BlockPos spawnPos, Player player) {

        BlockPos cakePos = spawnPos.relative(player.getDirection());
        while (level.isEmptyBlock(cakePos.below())) {
            cakePos = cakePos.below();
        }

        if (level.isEmptyBlock(cakePos)) {
            level.setBlock(cakePos, BlocksRegistry.overworld_cake.get().defaultBlockState(), 3);
        }

    }

}
