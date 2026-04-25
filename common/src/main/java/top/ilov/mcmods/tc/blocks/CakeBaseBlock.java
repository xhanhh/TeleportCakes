package top.ilov.mcmods.tc.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public abstract class CakeBaseBlock extends CakeBlock {

    public CakeBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                            @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()
                && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer) {

            if (isPassengerBlocked(serverPlayer)) {
                player.sendOverlayMessage(Component.translatable(getPassengerBlockedMessageKey()));
                return InteractionResult.PASS;
            }

            if (isTeleportBlocked(serverLevel)) {
                player.sendOverlayMessage(Component.translatable(getTeleportBlockedMessageKey()));
                return InteractionResult.PASS;
            }

            return teleport(serverLevel, pos, serverPlayer)
                    ? eat(level, pos, state, player)
                    : InteractionResult.FAIL;
        }

        return useAsCake(state, level, pos, player);
    }

    @NonNull
    protected InteractionResult useAsCake(@NonNull BlockState state, Level level, @NonNull BlockPos pos,
                                          @NonNull Player player) {
        if (level.isClientSide()) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

    @NonNull
    protected static InteractionResult eat(@NonNull LevelAccessor level, @NonNull BlockPos pos, @NonNull BlockState state,
                                           Player player) {

        player.awardStat(Stats.EAT_CAKE_SLICE);
        if (player.canEat(false)) {
            player.getFoodData().eat(2, 0.1F);
        }

        int i = state.getValue(BITES);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (i < 6) {
            level.setBlock(pos, state.setValue(BITES, i + 1), 3);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }

    protected boolean isPassengerBlocked(@NonNull ServerPlayer player) {
        return player.isPassenger();
    }

    @NonNull
    protected String getPassengerBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_while_riding";
    }

    protected abstract boolean isTeleportBlocked(@NonNull ServerLevel level);

    @NonNull
    protected abstract String getTeleportBlockedMessageKey();

    protected abstract boolean teleport(@NonNull ServerLevel level, @NonNull BlockPos pos,
                                        @NonNull ServerPlayer player);

}
