package top.lqsnow.lss.net;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static top.lqsnow.lss.LitematicaShulkerSupply.MOD_ID;

/**
 * Handshake response sent from server to client indicating whether the server
 * has the mod installed.
 * <p>
 * 服务端回应客户端的握手数据包，告知是否安装了本模组。
 */
public record HandshakeS2C(boolean ok) implements CustomPayload {
    public static final Id<HandshakeS2C> ID = new Id<>(Identifier.of(MOD_ID, "hello_s2c"));
    public static final PacketCodec<PacketByteBuf, HandshakeS2C> CODEC =
            PacketCodec.of((value, buf) -> buf.writeBoolean(value.ok), buf -> new HandshakeS2C(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
