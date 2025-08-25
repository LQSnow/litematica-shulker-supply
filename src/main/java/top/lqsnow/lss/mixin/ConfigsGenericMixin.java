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
 * 向 Litematica 原生的 Generic 配置选项列表追加本模组自定义的选项。
 */
@Mixin(value = fi.dy.masa.litematica.config.Configs.Generic.class, remap = false, priority = 900)
public abstract class ConfigsGenericMixin {
    @Shadow @Mutable public static ImmutableList<IConfigBase> OPTIONS;

    // 在 Generic 的 <clinit>（静态初始化）后，把我们的选项追加到 OPTIONS
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void lss$appendOwnOptions(CallbackInfo ci) {
        OPTIONS = new ImmutableList.Builder<IConfigBase>()
                .addAll(OPTIONS)
                .addAll(Configs.getOwnOptions()) // ← 只返回我们自己的条目（不要把原生再装进来）
                .build();
    }
}
