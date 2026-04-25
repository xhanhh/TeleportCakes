package top.ilov.mcmods.tc.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class FabricPayloads {

    private FabricPayloads() {}

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(
                AggregateCupcakePayload.TYPE,
                AggregateCupcakePayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                AggregateCupcakePayload.TYPE,
                (payload, context) -> context.server().execute(
                        () -> AggregateCupcakePayloadHandler.handleSelectionSync(payload, context.player())
                )
        );
    }
}
