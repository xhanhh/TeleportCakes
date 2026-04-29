package top.ilov.mcmods.tc.items;

import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;

import java.util.List;

public abstract class CupcakeBaseItem extends Item {

    protected CupcakeBaseItem(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        String blockedMessageKey = getBlockedMessageKey(level, player);
        if (blockedMessageKey != null) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(blockedMessageKey), true);
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        return super.use(level, player, hand);

    }

    @Override
    @NotNull
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {

        if (!(livingEntity instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return super.finishUsingItem(stack, level, livingEntity);
        }

        if (getBlockedMessageKey(serverLevel, player) != null) {
            return stack;
        }

        if (!teleport(serverLevel, player)) {
            return stack;
        }

        return super.finishUsingItem(stack, level, livingEntity);

    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()
                && TeleportCakesMod.CONFIG.isEnable_tooltips_for_shift_displaying_item()) {

            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable(getTooltipTranslationKey()));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.teleportcakes.shift"));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

    }

    @Nullable
    protected String getBlockedMessageKey(@NotNull Level level, @NotNull Player player) {

        if (player.isSpectator()) {
            return null;
        }

        if (player.isPassenger()) {
            return "msg.teleportcakes.cannot_eat_while_riding";
        }

        return isTeleportBlocked(level) ? getTeleportBlockedMessageKey() : null;

    }

    protected abstract boolean isTeleportBlocked(@NotNull Level level);

    @NotNull
    protected abstract String getTeleportBlockedMessageKey();

    @NotNull
    protected abstract String getTooltipTranslationKey();

    protected abstract boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player);

}