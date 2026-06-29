package top.ilov.mcmods.tc.events;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.items.AggregateCupcakeDestination;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;
import top.ilov.mcmods.tc.network.AggregateCupcakePayload;
import top.ilov.mcmods.tc.utils.ScrollWheelHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AggregateCupcakeEvent {

    private static final ScrollWheelHandler SCROLL_WHEEL_HANDLER = new ScrollWheelHandler();

    public static boolean handleScroll(long handle, double scrollDeltaX, double scrollDeltaY) {

        if (scrollDeltaX == 0.0D && scrollDeltaY == 0.0D) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (handle != minecraft.getWindow().getWindow()
                || minecraft.getOverlay() != null
                || minecraft.screen != null) {
            return false;
        }

        if (!minecraft.options.keyShift.isDown()) {
            return false;
        }

        LocalPlayer player = minecraft.player;
        if (player == null) {
            return false;
        }

        InteractionHand hand = findAggregateCupcakeHand(player);
        if (hand == null) {
            return false;
        }

        if (player.isUsingItem() && player.getUsedItemHand() == hand) {
            return true;
        }

        Vector2i scroll = SCROLL_WHEEL_HANDLER.onMouseScroll(
                normalizeScrollDelta(minecraft, scrollDeltaX),
                normalizeScrollDelta(minecraft, scrollDeltaY)
        );
        if (scroll.x == 0 && scroll.y == 0) {
            return true;
        }

        int scrollDelta = scroll.y != 0 ? scroll.y : -scroll.x;
        int delta = scrollDelta > 0 ? 1 : -1;

        ItemStack stack = player.getItemInHand(hand);

        int nextIndex = AggregateCupcakeDestination.fromIndex(
                AggregateCupcakeItem.getSelectedDestinationIndex(stack) + delta
        ).ordinal();

        AggregateCupcakeItem.setSelectedDestinationIndex(stack, nextIndex);
        player.displayClientMessage(AggregateCupcakeItem.getSelectedDestinationMessage(stack), true);

        if (minecraft.getConnection() != null) {
            minecraft.getConnection().send(new ServerboundCustomPayloadPacket(
                    new AggregateCupcakePayload(hand == InteractionHand.OFF_HAND, nextIndex)
            ));
        }

        return true;

    }

    private static double normalizeScrollDelta(Minecraft minecraft, double scrollDelta) {

        double adjustedDelta =
                minecraft.options.discreteMouseScroll().get() ? Math.signum(scrollDelta) : scrollDelta;

        return adjustedDelta * minecraft.options.mouseWheelSensitivity().get();

    }

    @Nullable
    private static InteractionHand findAggregateCupcakeHand(LocalPlayer player) {

        if (player.getMainHandItem().getItem() instanceof AggregateCupcakeItem) {
            return InteractionHand.MAIN_HAND;
        }

        if (player.getOffhandItem().getItem() instanceof AggregateCupcakeItem) {
            return InteractionHand.OFF_HAND;
        }

        return null;

    }
}
