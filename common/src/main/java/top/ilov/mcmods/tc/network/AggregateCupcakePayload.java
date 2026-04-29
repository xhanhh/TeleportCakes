package top.ilov.mcmods.tc.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.TeleportCakesMod;

public record AggregateCupcakePayload(boolean offHand, int index) implements CustomPacketPayload {

    public static final Type<AggregateCupcakePayload> TYPE =
            new Type<>(TeleportCakesMod.rl("aggregate_cupcake_selection"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AggregateCupcakePayload> STREAM_CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> {
                        ByteBufCodecs.BOOL.encode(buf, payload.offHand);
                        ByteBufCodecs.VAR_INT.encode(buf, payload.index);
                    },
                    buf -> new AggregateCupcakePayload(
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf)
                    )
            );

    public InteractionHand hand() {
        return offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    @Override
    @NotNull
    public Type<AggregateCupcakePayload> type() {
        return TYPE;
    }

}
