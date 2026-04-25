package top.ilov.mcmods.tc.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AggregateCupcakePayloadHandler {

    public static void handleSelectionSync(AggregateCupcakePayload payload, ServerPlayer player) {

        ItemStack stack = player.getItemInHand(payload.hand());
        if (stack.getItem() instanceof AggregateCupcakeItem) {
            AggregateCupcakeItem.setSelectedDestinationIndex(stack, payload.index());
        }

    }
}
