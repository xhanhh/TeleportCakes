package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.items.AggregateCupcakeDestination;
import top.ilov.mcmods.tc.utils.NetherTeleportHelper;

import java.util.List;

public class AggregateCupcakeItem extends Item {

    private static final String DESTINATION_INDEX_KEY = "AggregateDestinationIndex";
    private static final int COOLDOWN_TICKS = 10;

    public AggregateCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        String blockedMessageKey = getBlockedMessageKey(player);
        if (blockedMessageKey != null) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(blockedMessageKey), true);
            }
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide
                && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer
                && getSelectedDestination(stack) == AggregateCupcakeDestination.NETHER
                && NetherTeleportHelper.shouldShowPreparingSpawnMsg(serverLevel, serverPlayer)) {
            NetherTeleportHelper.showPreparingSpawnMsg(serverPlayer);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);

    }

    @Override
    @NotNull
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return stack;
        }

        if (getBlockedMessageKey(player) != null) {
            return stack;
        }

        if (getSelectedDestination(stack).teleport(serverLevel, player)) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return stack;
    }

    @Override
    @NotNull
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 20;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) {
            return;
        }

        tooltipComponents.add(Component.translatable(
                "tooltip.teleportcakes.aggregate_cupcake.selected",
                getSelectedDestination(stack).getDisplayName()
        ));

        if (TeleportCakesMod.CONFIG.isEnable_tooltips_for_shift_displaying_item()) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.aggregate_cupcake"));
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.aggregate_cupcake.unlimited"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.shift"));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    private String getBlockedMessageKey(@NotNull Player player) {

        if (player.isSpectator()) {
            return null;
        }

        if (player.isPassenger()) {
            return "msg.teleportcakes.cannot_eat_while_riding";
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return "msg.teleportcakes.aggregate_cupcake_on_cooldown";
        }

        return null;
    }

    @NotNull
    public static AggregateCupcakeDestination getSelectedDestination(@NotNull ItemStack stack) {
        return AggregateCupcakeDestination.fromIndex(getSelectedDestinationIndex(stack));
    }

    public static int getSelectedDestinationIndex(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(DESTINATION_INDEX_KEY) ? tag.getInt(DESTINATION_INDEX_KEY) : 0;
    }

    public static void setSelectedDestinationIndex(@NotNull ItemStack stack, int index) {
        AggregateCupcakeDestination destination = AggregateCupcakeDestination.fromIndex(index);
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt(DESTINATION_INDEX_KEY, destination.ordinal()));
    }

    @NotNull
    public static Component getSelectedDestinationMessage(@NotNull ItemStack stack) {
        return Component.translatable(
                "msg.teleportcakes.aggregate_cupcake.current_destination",
                getSelectedDestination(stack).getDisplayName()
        );
    }

}
