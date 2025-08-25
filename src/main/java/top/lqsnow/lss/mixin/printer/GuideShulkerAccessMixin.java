package top.lqsnow.lss.mixin.printer;

import me.aleksilassila.litematica.printer.guides.Guide;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;

import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;
import top.lqsnow.lss.mixin.AccessorLitematicaInventoryUtils;
import top.lqsnow.lss.printer.ShulkerScan;

@Mixin(value = Guide.class, priority = 900)
public abstract class GuideShulkerAccessMixin {

    @Invoker("getRequiredItems")
    protected abstract List<ItemStack> lss$invokeGetRequiredItems();

    @Unique
    private ItemStack lss$requiredFromShulker = null;

    @Inject(method = "getRequiredItem", at = @At("RETURN"), cancellable = true)
    private void lss$allowFromShulker(ClientPlayerEntity player, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        if (cir.getReturnValue().isPresent()) return;
        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;

        List<ItemStack> candidates = this.lss$invokeGetRequiredItems();
        ItemStack found = ShulkerScan.findFirstInAnyShulker(player, candidates);
        if (found != null) {
            lss$requiredFromShulker = found.copy();
            cir.setReturnValue(Optional.of(lss$requiredFromShulker));
        }
    }

    @Inject(method = "getRequiredItemStackSlot(Lnet/minecraft/client/network/ClientPlayerEntity;)I",
            at = @At("RETURN"), cancellable = true)
    private void lss$reserveHotbarSlot(ClientPlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() != -1) return;

        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;
        if (lss$requiredFromShulker == null || lss$requiredFromShulker.isEmpty()) return;

        int slot = AccessorLitematicaInventoryUtils.lss$invokeGetEmptyPickBlockableHotbarSlot(player.getInventory());
        if (slot == -1) slot = AccessorLitematicaInventoryUtils.lss$invokeGetPickBlockTargetSlot(player);
        if (slot == -1) return;

        ItemStack exist = player.getInventory().getStack(slot);
        if (!exist.isEmpty() && exist.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return;
        }
        cir.setReturnValue(slot);
    }
}
