package top.lqsnow.lss.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.util.ArrayList;
import java.util.List;

/**
 * mod 自身的配置项集合。
 */
public final class Configs {
    private Configs() {
    }

    private static final String KEY_BASE = LitematicaShulkerSupply.MOD_ID + ".config";
    private static final String GENERIC_K = KEY_BASE + ".generic";

    /** 是否启用潜影盒供应功能 */
    public static final ConfigBoolean ENABLED =
            new ConfigBoolean("shulker-supply-enabled", true).apply(GENERIC_K);

    /**
     * 返回本模组自定义的配置项列表，供 Litematica 注册。
     */
    public static ImmutableList<IConfigBase> getOwnOptions() {
        List<IConfigBase> list = new ArrayList<>();
        list.add(ENABLED);
        return ImmutableList.copyOf(list);
    }
}
