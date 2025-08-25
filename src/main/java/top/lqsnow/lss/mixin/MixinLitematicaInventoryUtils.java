package top.lqsnow.lss.mixin;

import net.minecraft.client.MinecraftClient;
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

    private static final ThreadLocal<ItemStack> LSS$REQUIRED = new ThreadLocal<>();

    @Inject(
            method = "schematicWorldPickBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lnet/minecraft/client/MinecraftClient;)V",
            at = @At("HEAD")
    )
    private static void lss$head(ItemStack required, BlockPos pos, World schematicWorld, MinecraftClient mc, CallbackInfo ci) {
        LSS$REQUIRED.set(required.copy());
    }

    @Inject(
            method = "schematicWorldPickBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lnet/minecraft/client/MinecraftClient;)V",
            at = @At("TAIL")
    )
    private static void lss$tail(ItemStack required, BlockPos pos, World schematicWorld, MinecraftClient mc, CallbackInfo ci) {
        try {
            // 条件：开关打开；服务端装了；非创造；且投影“pickBoxShulkers”为 false（避免冲突）
            if (!top.lqsnow.lss.config.Configs.ENABLED.getBooleanValue()) return;
            if (!LitematicaShulkerSupplyClient.SERVER_HAS_MOD) return;
            if (mc.player == null || mc.player.isCreative()) return;
            if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) return;

            ItemStack need = LSS$REQUIRED.get();
            if (need == null || need.isEmpty()) return;

            // 如果投影已经把所需物放到了“可用的手”（主手，或主手空且副手有），不干预
            if (EntityUtils.getUsedHandForItem(mc.player, need) != null) return;

            // 否则继续
            ShulkerSwapClientLogic.tryExtractFromShulkerAndSwap(mc, need);
        } finally {
            LSS$REQUIRED.remove();
        }
    }
}
