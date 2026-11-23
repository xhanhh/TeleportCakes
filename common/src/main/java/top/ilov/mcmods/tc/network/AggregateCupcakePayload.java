package top.ilov.mcmods.tc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import top.ilov.mcmods.tc.TeleportCakesMod;

public record AggregateCupcakePayload(boolean offHand, int index) {

    public static final ResourceLocation ID = TeleportCakesMod.rl("aggregate_cupcake_selection");

    public static void encode(AggregateCupcakePayload payload, FriendlyByteBuf buf) {
        buf.writeBoolean(payload.offHand);
        buf.writeVarInt(payload.index);
    }

    public static AggregateCupcakePayload decode(FriendlyByteBuf buf) {
        return new AggregateCupcakePayload(buf.readBoolean(), buf.readVarInt());
    }

    public InteractionHand hand() {
        return offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

}
