package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

public class OverworldCakeBlock extends CakeTeleportBase {

    public OverworldCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                           Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.MILK_BUCKET && state.getValue(BITES) > 0 && !level.isClientSide()) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                            @NotNull Player player, @NotNull BlockHitResult hitResult) {

        if (!level.isClientSide() && !player.isSpectator()) {
            if (level.dimension() != Level.OVERWORLD) {

                ResourceKey<Level> registryKey = Level.OVERWORLD;
                ServerLevel serverLevel = ((ServerLevel) level).getServer().getLevel(registryKey);

                if (serverLevel == null) {
                    return InteractionResult.FAIL;
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    TeleportTransition teleportTransition = serverPlayer.findRespawnPositionAndUseSpawnBlock(
                            false, TeleportTransition.DO_NOTHING
                    );
                    serverPlayer.teleport(teleportTransition);
                }

                return eat(level, pos, state, player);
            } else {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_overworld_cake"), true);
                return InteractionResult.PASS;
            }
        }

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


}
