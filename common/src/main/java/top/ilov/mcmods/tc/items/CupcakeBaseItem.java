package top.ilov.mcmods.tc.items;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;

import java.util.function.Consumer;

public abstract class CupcakeBaseItem extends Item {

    protected CupcakeBaseItem(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {

        String blockedMessageKey = getBlockedMessageKey(level, player);
        if (blockedMessageKey != null) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable(blockedMessageKey));
            }
            return InteractionResult.PASS;
        }

        return super.use(level, player, hand);

    }

    @Override
    @NonNull
    public ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity livingEntity) {

        if (!(livingEntity instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return super.finishUsingItem(stack, level, livingEntity);
        }

        if (getBlockedMessageKey(serverLevel, player) != null) {
            return stack;
        }

        if (!teleport(serverLevel, player)) {
            return stack;
        }

        return super.finishUsingItem(stack, livingEntity.level(), livingEntity);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
                                @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> components,
                                @NonNull TooltipFlag tooltipFlag) {

        if (TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) {
            if (Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable(getTooltipTranslationKey()));
            } else {
                components.accept(Component.translatable("tooltip.teleportcakes.shift"));
            }
        }

        super.appendHoverText(stack, context, tooltipDisplay, components, tooltipFlag);

    }

    @Nullable
    protected String getBlockedMessageKey(@NonNull Level level, @NonNull Player player) {

        if (player.isSpectator()) {
            return null;
        }

        if (player.isPassenger()) {
            return "msg.teleportcakes.cannot_eat_while_riding";
        }

        return isTeleportBlocked(level) ? getTeleportBlockedMessageKey() : null;

    }

    protected abstract boolean isTeleportBlocked(@NonNull Level level);

    @NonNull
    protected abstract String getTeleportBlockedMessageKey();

    @NonNull
    protected abstract String getTooltipTranslationKey();

    protected abstract boolean teleport(@NonNull ServerLevel level, @NonNull ServerPlayer player);

}
