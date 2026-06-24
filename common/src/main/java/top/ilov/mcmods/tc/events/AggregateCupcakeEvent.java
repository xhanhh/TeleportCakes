package top.ilov.mcmods.tc.events;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import top.ilov.mcmods.tc.items.AggregateCupcakeDestination;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;
import top.ilov.mcmods.tc.network.AggregateCupcakePayload;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AggregateCupcakeEvent {

    public static boolean handleScroll(double scrollDeltaX, double scrollDeltaY) {

        // 在macOS上shift+鼠标滚动是横向不是竖向。太坑了。
        double scrollDelta =
                scrollDeltaY != 0.0D ? scrollDeltaY : -scrollDeltaX;
        if (scrollDelta == 0.0D) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui.screen() != null) {
            return false;
        }

        if (!minecraft.hasShiftDown() && !minecraft.options.keyShift.isDown()) {
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

        ItemStack stack = player.getItemInHand(hand);
        int delta = scrollDelta > 0.0D ? 1 : -1;
        int nextIndex = AggregateCupcakeDestination.fromIndex(
                AggregateCupcakeItem.getSelectedDestinationIndex(stack) + delta
        ).ordinal();

        AggregateCupcakeItem.setSelectedDestinationIndex(stack, nextIndex);
        player.sendOverlayMessage(AggregateCupcakeItem.getSelectedDestinationMessage(stack));

        if (minecraft.getConnection() != null) {
            minecraft.getConnection().send(new ServerboundCustomPayloadPacket(
                    new AggregateCupcakePayload(hand == InteractionHand.OFF_HAND, nextIndex)
            ));
        }

        return true;

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
