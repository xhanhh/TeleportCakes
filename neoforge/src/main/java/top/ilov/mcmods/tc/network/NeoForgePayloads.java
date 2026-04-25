package top.ilov.mcmods.tc.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgePayloads {

    private NeoForgePayloads() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(
                        AggregateCupcakePayload.TYPE,
                        AggregateCupcakePayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> {
                            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                                AggregateCupcakePayloadHandler.handleSelectionSync(payload, player);
                            }
                        })
                );
    }
}
