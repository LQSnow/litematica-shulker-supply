package top.lqsnow.lss.net;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static top.lqsnow.lss.LitematicaShulkerSupply.MOD_ID;

/**
 * Handshake packet sent from client to server to check whether the server has
 * this mod installed.
 */
public record HandshakeC2S() implements CustomPayload {
    public static final Id<HandshakeC2S> ID = new Id<>(Identifier.of(MOD_ID, "hello_c2s"));
    public static final PacketCodec<PacketByteBuf, HandshakeC2S> CODEC =
            PacketCodec.of((value, buf) -> {}, buf -> new HandshakeC2S());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
