package top.lqsnow.lss.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.EntityUtils;
import top.lqsnow.lss.client.LitematicaShulkerSupplyClient;
import top.lqsnow.lss.client.ShulkerSwapClientLogic;

@Mixin(value = fi.dy.masa.litematica.util.InventoryUtils.class)
public abstract class MixinLitematicaInventoryUtils {

    @Inject(
            method = "schematicWorldPickBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lnet/minecraft/client/MinecraftClient;)V",
            at = @At("HEAD")
    )
    private static void lss$earlySupply(ItemStack required, BlockPos pos, World schematicWorld, MinecraftClient mc, CallbackInfo ci) {
        if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
        if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;
        if (mc.player == null || mc.player.isCreative()) return;
        if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;
        if (required == null || required.isEmpty()) return;

        if (fi.dy.masa.litematica.util.EntityUtils.getUsedHandForItem(mc.player, required) != null) return;

        if (lss$inventoryContains(mc, required)) return;

        top.lqsnow.lss.client.ShulkerSwapClientLogic.tryExtractFromShulkerAndSwap(mc, required);
    }

    private static boolean lss$inventoryContains(MinecraftClient mc, ItemStack required) {
        var player = mc.player;
        if (player == null) return false;

        // 主背包（含热栏）
        for (ItemStack s : player.getInventory().getMainStacks()) {
            if (!s.isEmpty() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreNbt(s, required)) {
                return true;
            }
        }
        // 副手
        ItemStack off = player.getOffHandStack();
        return !off.isEmpty() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreNbt(off, required);
    }

}
