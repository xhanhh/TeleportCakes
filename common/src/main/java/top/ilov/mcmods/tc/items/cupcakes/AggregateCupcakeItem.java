package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.items.AggregateCupcakeDestination;

import java.util.function.Consumer;

public class AggregateCupcakeItem extends Item {

    private static final String DESTINATION_INDEX_KEY = "AggregateDestinationIndex";
    private static final int COOLDOWN_TICKS = 10;

    public AggregateCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        String blockedMessageKey = getBlockedMessageKey(player, stack);
        if (blockedMessageKey != null) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable(blockedMessageKey));
            }
            return InteractionResult.PASS;
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;

    }

    @Override
    @NonNull
    public ItemStack finishUsingItem(@NonNull ItemStack stack,
                                     @NonNull Level level,
                                     @NonNull LivingEntity livingEntity) {

        if (!(livingEntity instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return stack;
        }

        if (getBlockedMessageKey(player, stack) != null) {
            return stack;
        }

        if (getSelectedDestination(stack).teleport(serverLevel, player)) {
            player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        }

        return stack;
    }

    @Override
    @NonNull
    public ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return 20;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
                                @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> components,
                                @NonNull TooltipFlag tooltipFlag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) {
            return;
        }

        components.accept(Component.translatable(
                "tooltip.teleportcakes.aggregate_cupcake.selected",
                getSelectedDestination(stack).getDisplayName()
        ));

        if (Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.teleportcakes.aggregate_cupcake"));
        } else {
            components.accept(Component.translatable("tooltip.teleportcakes.shift"));
        }

        super.appendHoverText(stack, context, tooltipDisplay, components, tooltipFlag);

    }

    @Nullable
    private String getBlockedMessageKey(@NonNull Player player, @NonNull ItemStack stack) {

        if (player.isSpectator()) {
            return null;
        }

        if (player.isPassenger()) {
            return "msg.teleportcakes.cannot_eat_while_riding";
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            return "msg.teleportcakes.aggregate_cupcake_on_cooldown";
        }

        return null;

    }

    @NonNull
    public static AggregateCupcakeDestination getSelectedDestination(@NonNull ItemStack stack) {
        return AggregateCupcakeDestination.fromIndex(getSelectedDestinationIndex(stack));
    }

    public static int getSelectedDestinationIndex(@NonNull ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getIntOr(DESTINATION_INDEX_KEY, 0);
    }

    public static void setSelectedDestinationIndex(@NonNull ItemStack stack, int index) {
        AggregateCupcakeDestination destination = AggregateCupcakeDestination.fromIndex(index);
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt(DESTINATION_INDEX_KEY, destination.ordinal()));
    }

    @NonNull
    public static Component getSelectedDestinationMessage(@NonNull ItemStack stack) {
        return Component.translatable(
                "message.teleportcakes.aggregate_cupcake.current_destination",
                getSelectedDestination(stack).getDisplayName()
        );
    }
}
