package top.lqsnow.lss.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ClientConfigs {
    private ClientConfigs(){}

    private static final String KEY_BASE  = LitematicaShulkerSupply.MOD_ID + ".config";
    private static final String GENERIC_K = KEY_BASE + ".generic";

    /** 总开关（仅客户端 UI/配置展示用） */
    public static final ConfigBoolean ENABLED =
            new ConfigBoolean("enabled", true).apply(GENERIC_K);

    /** 仅返回本模组自己的条目，供 GUI 追加 */
    public static ImmutableList<IConfigBase> getOwnOptions() {
        List<IConfigBase> list = new ArrayList<>();
        list.add(ENABLED);
        return ImmutableList.copyOf(list);
    }
}
