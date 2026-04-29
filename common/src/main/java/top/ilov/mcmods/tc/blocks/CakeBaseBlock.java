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
import org.jetbrains.annotations.NotNull;

public abstract class CakeBaseBlock extends CakeBlock {

    public CakeBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                            @NotNull Player player, @NotNull BlockHitResult hitResult) {

        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide
                && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer) {

            if (isPassengerBlocked(serverPlayer)) {
                serverPlayer.displayClientMessage(Component.translatable(getPassengerBlockedMessageKey()), true);
                return InteractionResult.PASS;
            }

            if (isTeleportBlocked(serverLevel)) {
                serverPlayer.displayClientMessage(Component.translatable(getTeleportBlockedMessageKey()), true);
                return InteractionResult.PASS;
            }

            return teleport(serverLevel, pos, serverPlayer)
                    ? eat(level, pos, state, player)
                    : InteractionResult.FAIL;
        }

        return useAsCake(state, level, pos, player);

    }

    @NotNull
    protected InteractionResult useAsCake(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player) {

        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);

    }

    @NotNull
    protected static InteractionResult eat(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state,
                                           Player player) {

        player.awardStat(Stats.EAT_CAKE_SLICE);
        if (player.canEat(false)) {
            player.getFoodData().eat(2, 0.1F);
        }

        int bites = state.getValue(BITES);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites < 6) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;

    }

    protected boolean isPassengerBlocked(@NotNull ServerPlayer player) {
        return player.isPassenger();
    }

    @NotNull
    protected String getPassengerBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_while_riding";
    }

    protected abstract boolean isTeleportBlocked(@NotNull ServerLevel level);

    @NotNull
    protected abstract String getTeleportBlockedMessageKey();

    protected abstract boolean teleport(@NotNull ServerLevel level, @NotNull BlockPos pos,
                                        @NotNull ServerPlayer player);

}