package top.lqsnow.lss.net;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static top.lqsnow.lss.LitematicaShulkerSupply.MOD_ID;

public record SwapFromShulkerC2S(int boxContainerSlotId, int innerIndex, int destHotbarSlot) implements CustomPayload {

    public static final Id<SwapFromShulkerC2S> ID = new Id<>(Identifier.of(MOD_ID, "swap_from_shulker"));
    public static final PacketCodec<PacketByteBuf, SwapFromShulkerC2S> CODEC =
            PacketCodec.of(
                    (value, buf) -> {
                        buf.writeVarInt(value.boxContainerSlotId);
                        buf.writeVarInt(value.innerIndex);
                        buf.writeVarInt(value.destHotbarSlot);
                    },
                    buf -> new SwapFromShulkerC2S(buf.readVarInt(), buf.readVarInt(), buf.readVarInt())
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
