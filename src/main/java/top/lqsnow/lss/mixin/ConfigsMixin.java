package top.lqsnow.lss.mixin;

import fi.dy.masa.litematica.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lqsnow.lss.LitematicaShulkerSupply;

/**
 * 在 Litematica 读取和保存配置时同步本模组的配置文件。
 */
@Mixin(value = Configs.class, remap = false, priority = 500)
public class ConfigsMixin {

    @Inject(method = "loadFromFile", at = @At("TAIL"))
    private static void lss$loadOwn(CallbackInfo ci) {
        try {
            LitematicaShulkerSupply.CONFIG.load();
        } catch (Exception e) {
            // 已在管理器里做了日志，这里不再重复抛异常
        }
    }

    @Inject(method = "saveToFile", at = @At("TAIL"))
    private static void lss$saveOwn(CallbackInfo ci) {
        LitematicaShulkerSupply.CONFIG.save();
    }
}
