package top.lqsnow.lss.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
// 先不引入 Integer/Double，后续要扩展再加
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.util.List;

public final class Configs {
    private Configs() {}

    // 翻译 key 基础：litematica-shulker-supply.config.generic.*
    private static final String KEY_BASE   = LitematicaShulkerSupply.MOD_ID + ".config";
    private static final String GENERIC_K  = KEY_BASE + ".generic";

    /** 总开关：是否启用“从背包潜影盒自动补货” */
    public static final ConfigBoolean ENABLED =
            new ConfigBoolean("enabled", true).apply(GENERIC_K);

    // —— 下面这些是预留位，将来要扩展时直接取消注释即可 —— //
    // public static final ConfigBoolean HOTBAR_ONLY =
    //        new ConfigBoolean("hotbarOnly", true).apply(GENERIC_K);
    // public static final ConfigInteger MAX_WITHDRAW_PER_TICK =
    //        new ConfigInteger("maxWithdrawPerTick", 32, 1, 64).apply(GENERIC_K);
    // public static final ConfigInteger EXCHANGE_COOLDOWN_MS =
    //        new ConfigInteger("exchangeCooldownMs", 80, 0, 1000).apply(GENERIC_K);
    // public static final ConfigBoolean DEBUG_LOGS =
    //        new ConfigBoolean("debugLogs", false).apply(GENERIC_K);

    /** 返回要并入 Litematica “Generic” 页的配置列表 */
    public static ImmutableList<IConfigBase> getConfigList() {
        List<IConfigBase> list = new java.util.ArrayList<>(fi.dy.masa.litematica.config.Configs.Generic.OPTIONS);
        list.add(ENABLED);
        return ImmutableList.copyOf(list);
    }

    // 如果你之后要做热键页，也做一个 hotkeys() 返回 List<IHotkey>
    // public static ImmutableList<IHotkey> hotkeys() { ... }
}
