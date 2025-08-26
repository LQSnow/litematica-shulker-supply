// top/lqsnow/lss/mixin/ConfigsGenericMixin.java
package top.lqsnow.lss.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lqsnow.lss.config.Configs;

/**
 * Adds this mod's custom options to Litematica's native Generic configuration
 * list.
 */
@Mixin(value = fi.dy.masa.litematica.config.Configs.Generic.class, remap = false, priority = 900)
public abstract class ConfigsGenericMixin {
    @Shadow @Mutable public static ImmutableList<IConfigBase> OPTIONS;

    // After Generic's <clinit> (static initialization), append our options to OPTIONS
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void lss$appendOwnOptions(CallbackInfo ci) {
        OPTIONS = new ImmutableList.Builder<IConfigBase>()
                .addAll(OPTIONS)
                .addAll(Configs.getOwnOptions()) // ← returns only our entries (do not re-add originals)
                .build();
    }
}
