package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.blocks.CakeBaseBlock;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

import java.util.List;

public class EndCakeBlock extends CakeBaseBlock {

    public EndCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                           Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.ENDER_EYE && state.getValue(BITES) > 0 && !level.isClientSide) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    }

    @Override
    protected boolean isTeleportBlocked(@NotNull ServerLevel level) {
        return level.dimension().equals(Level.END);
    }

    @Override
    @NotNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_end_cake";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;

        if (TeleportCakesMod.CONFIG.isEnable_tooltips_for_shift_displaying_item()) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.end_cake"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.shift"));
            }
        }

    }

    @Override
    protected boolean teleport(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ServerPlayer player) {
        return CakeTeleporter.teleportToEnd(level, player);
    }

}
