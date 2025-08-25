package top.lqsnow.lss.mixin.printer;

import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;
import top.lqsnow.lss.client.ShulkerSwapClientLogic;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.EntityUtils;

/**
 * 在打印机执行准备动作时，提前尝试从潜影盒中补充所需物品。
 */
@Mixin(value = PrepareAction.class, remap = false)
public abstract class PrepareActionSupplyMixin {

    /**
     * 在打印机真正改槽/转头/交互之前，尝试把“所需物品”从潜影盒整格互换到快捷栏（并选中）。
     * 失败不拦截原行为。
     */
    @Inject(method = "send(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ClientPlayerEntity;)V",
            at = @At("HEAD"))
    private void lss$trySupplyBeforeSend(MinecraftClient client, ClientPlayerEntity player, CallbackInfo ci) {
        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return; // 与原生冲突则让原生生效
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;

        // 拿到 PrinterPlacementContext 中准备的“所需物品”
        PrepareAction self = (PrepareAction)(Object)this;
        PrinterPlacementContext ctx = self.context;
        if (ctx == null) return;

        ItemStack required = ctx.getStack();
        if (required == null || required.isEmpty()) return;

        // 如果手上已经有（主/副手） -> 不处理
        if (EntityUtils.getUsedHandForItem(player, required) != null) return;

        // 交给你现成的整格互换逻辑（含客户端预测 + C2S + 服务器校验 + 同步）
        ShulkerSwapClientLogic.tryExtractFromShulkerAndSwap(client, required);
    }
}
