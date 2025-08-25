package top.lqsnow.lss.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Configs.class, remap = false)
public class ConfigsMixin {

    // 覆盖 Litematica 在 loadFromFile 时取 Generic.OPTIONS 的那次字段读取
    @Redirect(
            method = "loadFromFile",
            at = @At(value = "FIELD",
                    target = "Lfi/dy/masa/litematica/config/Configs$Generic;OPTIONS:Lcom/google/common/collect/ImmutableList;")
    )
    private static ImmutableList<IConfigBase> lss$load_genericOptions() {
        return top.lqsnow.lss.config.Configs.getConfigList();
    }

    // 覆盖 saveToFile 时使用的 Generic.OPTIONS
    @Redirect(
            method = "saveToFile",
            at = @At(value = "FIELD",
                    target = "Lfi/dy/masa/litematica/config/Configs$Generic;OPTIONS:Lcom/google/common/collect/ImmutableList;")
    )
    private static ImmutableList<IConfigBase> lss$save_genericOptions() {
        return top.lqsnow.lss.config.Configs.getConfigList();
    }
}