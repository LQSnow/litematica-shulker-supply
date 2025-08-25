package top.lqsnow.lss.printer;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

/**
 * 与打印机相关的潜影盒扫描工具。
 */
public final class ShulkerScan {
    private ShulkerScan() {}

    /**
     * 在玩家可见容器（自身背包面板）里，查找第一只包含 candidates 中任一物品的潜影盒；
     * 找到则返回盒内真实的 ItemStack 副本；找不到返回 null。
     */
    public static ItemStack findFirstInAnyShulker(ClientPlayerEntity player, List<ItemStack> candidates) {
        if (player == null || candidates == null || candidates.isEmpty()) return null;

        PlayerScreenHandler handler = player.playerScreenHandler;
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot s = handler.slots.get(i);
            ItemStack st = s.getStack();
            if (st.isEmpty()) continue;
            if (!(st.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) continue;

            ContainerComponent container = st.getComponents().get(DataComponentTypes.CONTAINER);
            if (container == null) continue;

            DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
            container.copyTo(list);

            for (ItemStack inner : list) {
                if (inner.isEmpty()) continue;
                for (ItemStack cand : candidates) {
                    if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreNbt(inner, cand)) {
                        return inner.copy();
                    }
                }
            }
        }
        return null;
    }
}
