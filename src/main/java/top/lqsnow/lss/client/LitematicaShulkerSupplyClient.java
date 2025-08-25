package top.lqsnow.lss.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import top.lqsnow.lss.net.HandshakeC2S;
import top.lqsnow.lss.net.HandshakeS2C;

@Environment(EnvType.CLIENT)
public class LitematicaShulkerSupplyClient implements ClientModInitializer {

    public static volatile boolean SERVER_HAS_MOD = false;

    @Override
    public void onInitializeClient() {
        // 进服即发起握手
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SERVER_HAS_MOD = false;
            ClientPlayNetworking.send(new HandshakeC2S());
        });

        // 收到握手回应
        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2C.ID, (payload, context) -> {
            SERVER_HAS_MOD = payload.ok();
        });
    }
}
