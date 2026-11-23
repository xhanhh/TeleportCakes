package top.ilov.mcmods.tc.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public final class FabricPayloads {

    private FabricPayloads() {}

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                AggregateCupcakePayload.ID,
                (server, player, handler, buf, responseSender) -> {
                    AggregateCupcakePayload payload = AggregateCupcakePayload.decode(buf);
                    server.execute(() -> AggregateCupcakePayloadHandler.handleSelectionSync(payload, player));
                }
        );
    }

    public static void send(AggregateCupcakePayload payload) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        AggregateCupcakePayload.encode(payload, buf);
        ClientPlayNetworking.send(AggregateCupcakePayload.ID, buf);
    }

}
