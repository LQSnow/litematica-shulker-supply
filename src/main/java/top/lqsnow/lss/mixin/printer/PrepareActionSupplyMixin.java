package top.lqsnow.lss.mixin.printer;

import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;
import top.lqsnow.lss.client.ShulkerSwapClientLogic;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.EntityUtils;

/**
 * During the printer's prepare action, attempt to supply required items from
 * shulker boxes ahead of time.
 */
@Mixin(value = PrepareAction.class)
public abstract class PrepareActionSupplyMixin {

    @Final
    @Shadow
    public PrinterPlacementContext context; // Shadowed target field

    @Inject(method = "send(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ClientPlayerEntity;)V",
            at = @At("HEAD"))
    private void lss$trySupplyBeforeSend(MinecraftClient client, ClientPlayerEntity player, CallbackInfo ci) {

        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (fi.dy.masa.litematica.config.Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;

        PrinterPlacementContext ctx = this.context;
        if (ctx == null) return;

        ItemStack required = ctx.getStack();
        if (required == null || required.isEmpty()) return;
        if (fi.dy.masa.litematica.util.EntityUtils.getUsedHandForItem(player, required) != null) return;

        ShulkerSwapClientLogic.tryExtractFromShulkerAndSwap(client, required);
    }
}
