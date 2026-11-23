package top.ilov.mcmods.tc.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;

public final class AggregateCupcakePayloadHandler {

    private AggregateCupcakePayloadHandler() {}

    public static void handleSelectionSync(AggregateCupcakePayload payload, ServerPlayer player) {

        ItemStack stack = player.getItemInHand(payload.hand());
        if (stack.getItem() instanceof AggregateCupcakeItem) {
            AggregateCupcakeItem.setSelectedDestinationIndex(stack, payload.index());
        }

    }
}
