package top.lqsnow.lss.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.util.ArrayList;
import java.util.List;

public final class Configs {
    private Configs() {
    }

    private static final String KEY_BASE = LitematicaShulkerSupply.MOD_ID + ".config";
    private static final String GENERIC_K = KEY_BASE + ".generic";

    public static final ConfigBoolean ENABLED =
            new ConfigBoolean("shulker-supply-enabled", true).apply(GENERIC_K);

    public static ImmutableList<IConfigBase> getOwnOptions() {
        List<IConfigBase> list = new ArrayList<>();
        list.add(ENABLED);
        return ImmutableList.copyOf(list);
    }
}
