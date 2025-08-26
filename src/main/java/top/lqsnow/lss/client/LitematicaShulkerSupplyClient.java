package top.lqsnow.lss.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import top.lqsnow.lss.LitematicaShulkerSupply;
import top.lqsnow.lss.config.ConfigManager;
import top.lqsnow.lss.net.HandshakeC2S;
import top.lqsnow.lss.net.HandshakeS2C;
import top.lqsnow.lss.net.SwapFromShulkerC2S;

/**
 * Client entry point. Performs a handshake with the server and records
 * whether the server has this mod installed.
 */
@Environment(EnvType.CLIENT)
public class LitematicaShulkerSupplyClient implements ClientModInitializer {

    public static final ConfigManager CONFIG = new ConfigManager(
            FabricLoader.getInstance().getConfigDir().resolve(LitematicaShulkerSupply.MOD_ID + ".json")
    );

    /** Flag indicating whether the current server has the mod installed */
    public static volatile boolean SERVER_HAS_MOD = false;

    /**
     * Initialize the client by registering handshake logic and performing
     * checks when joining a world.
     */
    @Override
    public void onInitializeClient() {
        try {
            CONFIG.load();
        } catch (Exception e) {
            LitematicaShulkerSupply.LOGGER.warn("[{}] Failed to pre-load config: {}", LitematicaShulkerSupply.MOD_ID, e.getMessage());
        }

        // On join: perform handshake
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SERVER_HAS_MOD = false;
            if (ClientPlayNetworking.canSend(HandshakeC2S.ID)) {
                ClientPlayNetworking.send(new HandshakeC2S());
            }
        });

        // Reset on disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            SERVER_HAS_MOD = false;
        });

        // Receive handshake response
        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2C.ID, (payload, context) -> {
            SERVER_HAS_MOD = payload.ok();
        });
    }
}
