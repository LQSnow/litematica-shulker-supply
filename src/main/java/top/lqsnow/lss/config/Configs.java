package top.lqsnow.lss.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of configuration options for the mod.
 */
public final class Configs {
    private Configs() {
    }

    private static final String GENERIC = LitematicaShulkerSupply.MOD_ID + ".config.generic";

    /** Whether the shulker supply feature is enabled */
    public static final ConfigBoolean ENABLED =
            new ConfigBoolean("shulkerSupply", true).apply(GENERIC);

    /**
     * Return the list of custom configuration options for registration with
     * Litematica.
     */
    public static ImmutableList<IConfigBase> getOwnOptions() {
        List<IConfigBase> list = new ArrayList<>();
        list.add(ENABLED);
        return ImmutableList.copyOf(list);
    }
}
