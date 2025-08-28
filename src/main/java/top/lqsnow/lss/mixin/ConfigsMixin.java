package top.lqsnow.lss.mixin;

import fi.dy.masa.litematica.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;

/**
 * Synchronizes this mod's config file when Litematica loads or saves its own
 * configuration.
 */
@Mixin(value = Configs.class, remap = false, priority = 500)
public class ConfigsMixin {

    @Inject(method = "loadFromFile", at = @At("TAIL"))
    private static void lss$loadOwn(CallbackInfo ci) {
        try {
            LitematicaShulkerSupplyClient.CONFIG.load();
        } catch (Exception e) {
            // Logging handled in the manager; suppress exception
        }
    }

    @Inject(method = "saveToFile", at = @At("TAIL"))
    private static void lss$saveOwn(CallbackInfo ci) {
        LitematicaShulkerSupplyClient.CONFIG.save();
    }
}
