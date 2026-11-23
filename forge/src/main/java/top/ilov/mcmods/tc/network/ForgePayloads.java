package top.ilov.mcmods.tc.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgePayloads {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            AggregateCupcakePayload.ID, () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ForgePayloads() {}

    public static void register() {
        CHANNEL.messageBuilder(AggregateCupcakePayload.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AggregateCupcakePayload::encode)
                .decoder(AggregateCupcakePayload::decode)
                .consumerMainThread((payload, context) -> {
                    ServerPlayer player = context.get().getSender();
                    if (player != null) {
                        AggregateCupcakePayloadHandler.handleSelectionSync(payload, player);
                    }
                })
                .add();
    }

    public static void send(AggregateCupcakePayload payload) {
        CHANNEL.sendToServer(payload);
    }

}
