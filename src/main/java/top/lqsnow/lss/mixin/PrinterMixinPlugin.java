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
        // Try both potential mod IDs; fallback to checking class existence
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
        return null; // use default refmap
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Enable printer-specific mixins only if the printer mod is present
        if (mixinClassName.endsWith("printer.GuideShulkerAccessMixin")
                || mixinClassName.endsWith("printer.PrepareActionSupplyMixin")) {
            return hasPrinter;
        }
        return true;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        // Return null to let Mixin use the list from the JSON
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
