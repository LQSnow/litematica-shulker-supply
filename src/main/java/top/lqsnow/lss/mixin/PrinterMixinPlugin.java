package top.lqsnow.lss.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class PrinterMixinPlugin implements IMixinConfigPlugin {
    private boolean hasPrinter;

    @Override
    public void onLoad(String mixinPackage) {
        // 两种可能的 modid 都试一下；再兜底用类存在性判断
        this.hasPrinter =
                FabricLoader.getInstance().isModLoaded("litematica-printer")
                        || FabricLoader.getInstance().isModLoaded("litematica_printer")
                        || classExists("me.aleksilassila.litematica.printer.guides.Guide");
    }

    private static boolean classExists(String cn) {
        try {
            Class.forName(cn, false, PrinterMixinPlugin.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null; // 用默认 refmap
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // 仅当装了 Printer 时，才启用对其的适配 mixin
        if (mixinClassName.endsWith("printer.GuideShulkerAccessMixin")) {
            return hasPrinter;
        }
        return true;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        // 返回 null 让 Mixin 使用 json 里的列表
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }
}
