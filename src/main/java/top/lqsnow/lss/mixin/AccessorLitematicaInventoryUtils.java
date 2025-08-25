package top.lqsnow.lss.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 通过 Invoker 暴露 Litematica 自带的 InventoryUtils 的私有方法，供本模组复用。
 */
@Mixin(value = fi.dy.masa.litematica.util.InventoryUtils.class)
public interface AccessorLitematicaInventoryUtils {

    /** 调用 InventoryUtils#getEmptyPickBlockableHotbarSlot */
    @Invoker("getEmptyPickBlockableHotbarSlot")
    static int lss$invokeGetEmptyPickBlockableHotbarSlot(PlayerInventory inventory) {
        throw new AssertionError();
    }

    /** 调用 InventoryUtils#getPickBlockTargetSlot */
    @Invoker("getPickBlockTargetSlot")
    static int lss$invokeGetPickBlockTargetSlot(PlayerEntity player) {
        throw new AssertionError();
    }

    /** 调用 InventoryUtils#canPickToSlot */
    @Invoker("canPickToSlot")
    static boolean lss$invokeCanPickToSlot(PlayerInventory inventory, int slotNum) {
        throw new AssertionError();
    }
}
