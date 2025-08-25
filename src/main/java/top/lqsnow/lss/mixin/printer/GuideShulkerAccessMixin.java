package top.lqsnow.lss.mixin.printer;

import me.aleksilassila.litematica.printer.guides.Guide;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;
import top.lqsnow.lss.mixin.AccessorLitematicaInventoryUtils;
import top.lqsnow.lss.printer.ShulkerScan;

import java.util.List;
import java.util.Optional;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;

@Mixin(value = Guide.class, remap = false)
public abstract class GuideShulkerAccessMixin {

    // 使用 @Shadow 暴露受保护方法，方便在 mixin 中调用
    @Shadow
    protected abstract List<ItemStack> getRequiredItems();

    @Unique
    private ItemStack lss$requiredFromShulker = null;

    @Inject(method = "getRequiredItem(Lnet/minecraft/client/network/ClientPlayerEntity;)Ljava/util/Optional;",
            at = @At("RETURN"), cancellable = true)
    private void lss$allowFromShulker(ClientPlayerEntity player, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        // 原方法已经找到了，或前置条件不满足 → 不改
        if (cir.getReturnValue().isPresent()) return;
        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return; // 避免与投影原生冲突
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;

        // 用 @Shadow 到的受保护 API 拿候选物列表
        List<ItemStack> candidates = this.getRequiredItems();
        ItemStack found = ShulkerScan.findFirstInAnyShulker(player, candidates);
        if (found != null) {
            lss$requiredFromShulker = found.copy();
            cir.setReturnValue(Optional.of(lss$requiredFromShulker));
        }
    }

    @Inject(method = "getRequiredItemStackSlot(Lnet/minecraft/client/network/ClientPlayerEntity;)I",
            at = @At("RETURN"), cancellable = true)
    private void lss$reserveHotbarSlot(ClientPlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        if (original != -1) return;

        if (player == null || player.isCreative()) return;
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;
        if (lss$requiredFromShulker == null || lss$requiredFromShulker.isEmpty()) return;

        // 复用 Litematica 的“挑槽”逻辑
        int slot = AccessorLitematicaInventoryUtils.lss$invokeGetEmptyPickBlockableHotbarSlot(player.getInventory());
        if (slot == -1) {
            slot = AccessorLitematicaInventoryUtils.lss$invokeGetPickBlockTargetSlot(player);
        }
        if (slot == -1) return;

        // 目标槽不能是潜影盒
        ItemStack exist = player.getInventory().getStack(slot);
        if (!exist.isEmpty() && exist.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return;
        }

        cir.setReturnValue(slot);
    }
}
