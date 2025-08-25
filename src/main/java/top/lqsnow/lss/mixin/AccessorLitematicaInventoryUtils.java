package top.lqsnow.lss.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = fi.dy.masa.litematica.util.InventoryUtils.class)
public interface AccessorLitematicaInventoryUtils {

    @Invoker("getEmptyPickBlockableHotbarSlot")
    static int lss$invokeGetEmptyPickBlockableHotbarSlot(PlayerInventory inventory) {
        throw new AssertionError();
    }

    @Invoker("getPickBlockTargetSlot")
    static int lss$invokeGetPickBlockTargetSlot(PlayerEntity player) {
        throw new AssertionError();
    }

    @Invoker("canPickToSlot")
    static boolean lss$invokeCanPickToSlot(PlayerInventory inventory, int slotNum) {
        throw new AssertionError();
    }
}
