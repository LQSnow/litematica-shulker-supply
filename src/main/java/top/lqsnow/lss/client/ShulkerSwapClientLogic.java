package top.lqsnow.lss.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import top.lqsnow.lss.mixin.AccessorLitematicaInventoryUtils;
import top.lqsnow.lss.net.SwapFromShulkerC2S;

/**
 * Core client-side logic for swapping items between shulker boxes and the
 * player's hotbar.
 * <p>
 * 客户端关于潜影盒整格互换的核心逻辑封装。
 */
public class ShulkerSwapClientLogic {


    /**
     * 尝试从潜影盒中取出匹配的物品并与指定快捷栏槽位整格互换。
     * 成功时会进行客户端预测并向服务端发送确认数据包。
     *
     * @param mc       当前客户端实例
     * @param required 需要的物品
     * @return 是否成功完成互换
     */
    public static boolean tryExtractFromShulkerAndSwap(MinecraftClient mc, ItemStack required) {
        PlayerEntity player = mc.player;
        if (player == null || required == null || required.isEmpty()) return false;

        PlayerScreenHandler handler = player.playerScreenHandler;
        PlayerInventory inv = player.getInventory();

        // 1) 找“第一只包含目标物品”的潜影盒：返回 (容器槽位ID, 盒内索引)
        ShulkerHit hit = findShulkerWithRequired(handler, required);
        if (hit == null) return false;

        // 2) 选择目标快捷栏槽位（沿用投影规则 + 该槽不能是潜影盒）
        int hotbarSlot = pickHotbarTarget(inv, player);
        if (hotbarSlot < 0) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return false;
        }

        ItemStack fromBox = hit.innerCopy();
        ItemStack destExisting = inv.getStack(hotbarSlot).copy();

        // 禁止“潜影盒进潜影盒”
        if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return false;
        }

        // 3) 同帧客户端预测：整格互换（盒内 innerIndex <-> 快捷栏 hotbarSlot）
        inv.getMainStacks().set(hotbarSlot, fromBox.copy());
        inv.setSelectedSlot(hotbarSlot);

        DefaultedList<ItemStack> list = hit.list;
        list.set(hit.innerIndex, destExisting.copy());
        ItemStack boxStack = hit.boxSlot.getStack().copy();
        boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
        hit.boxSlot.setStack(boxStack);

        // 4) 发包让服务端确认
        ClientPlayNetworking.send(new SwapFromShulkerC2S(hit.containerSlotId, hit.innerIndex, hotbarSlot));

        // 记录限速（与投影一致）
        fi.dy.masa.litematica.util.WorldUtils.setEasyPlaceLastPickBlockTime();
        return true;
    }

    /**
     * 在当前容器中查找第一只包含所需物品的潜影盒。
     */
    private static ShulkerHit findShulkerWithRequired(PlayerScreenHandler handler, ItemStack required) {
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot s = handler.slots.get(i);
            ItemStack st = s.getStack();
            if (st.isEmpty()) continue;
            if (!(st.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) continue;

            ContainerComponent container = st.getComponents().get(DataComponentTypes.CONTAINER);
            if (container == null) continue;

            DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
            container.copyTo(list);

            int inner = firstIndexMatch(list, required);
            if (inner >= 0) {
                return new ShulkerHit(i, s, list, inner);
            }
        }
        return null;
    }

    /**
     * 在潜影盒物品列表中查找第一处与目标物品匹配的索引。
     */
    private static int firstIndexMatch(DefaultedList<ItemStack> list, ItemStack required) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack it = list.get(i);
            if (!it.isEmpty() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreNbt(it, required)) {
                return i; // 第一格匹配
            }
        }
        return -1;
    }

    /**
     * 选择目标快捷栏槽位，要求该槽位可用于 pick block 且不是潜影盒。
     */
    private static int pickHotbarTarget(PlayerInventory inv, PlayerEntity player) {
        int slot = AccessorLitematicaInventoryUtils.lss$invokeGetEmptyPickBlockableHotbarSlot(inv);
        if (slot == -1) {
            slot = AccessorLitematicaInventoryUtils.lss$invokeGetPickBlockTargetSlot(player);
        }
        if (slot == -1) return -1;

        ItemStack exist = inv.getStack(slot);
        if (!exist.isEmpty() && exist.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            return -1; // 新增的“目标槽不能是潜影盒”的约束
        }
        return slot;
    }

    /**
     * 记录一次命中的潜影盒及其相关信息。
     */
    private record ShulkerHit(int containerSlotId, Slot boxSlot, DefaultedList<ItemStack> list, int innerIndex) {
        ItemStack innerCopy() { return list.get(innerIndex).copy(); }
    }

    // === 新增：仅检查“潜影盒里是否有目标物”（不做交换） ===
    public static boolean hasInAnyShulker(PlayerEntity player, ItemStack required) {
        if (player == null || required == null || required.isEmpty()) return false;

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

            if (firstIndexMatch(list, required) >= 0) {
                return true;
            }
        }
        return false;
    }

    // === 新增：强制把物品换到“指定热栏槽位” ===
    public static boolean tryExtractFromShulkerAndSwapToSlot(MinecraftClient mc, ItemStack required, int hotbarSlot) {
        PlayerEntity player = mc.player;
        if (player == null || required == null || required.isEmpty()) return false;
        if (!PlayerInventory.isValidHotbarIndex(hotbarSlot)) return false;

        PlayerScreenHandler handler = player.playerScreenHandler;

        // 目标槽不能是潜影盒
        ItemStack destExisting = player.getInventory().getStack(hotbarSlot);
        if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            return false;
        }

        // 找第一只匹配的潜影盒
        ShulkerHit hit = findShulkerWithRequired(handler, required);
        if (hit == null) return false;

        // 客户端预测
        ItemStack fromBox = hit.innerCopy();
        player.getInventory().getMainStacks().set(hotbarSlot, fromBox.copy());
        player.getInventory().setSelectedSlot(hotbarSlot);

        DefaultedList<ItemStack> list = hit.list;
        list.set(hit.innerIndex, destExisting.copy());
        ItemStack boxStack = hit.boxSlot.getStack().copy();
        boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
        hit.boxSlot.setStack(boxStack);

        // 发包给服务端
        ClientPlayNetworking.send(new SwapFromShulkerC2S(hit.containerSlotId, hit.innerIndex, hotbarSlot));

        fi.dy.masa.litematica.util.WorldUtils.setEasyPlaceLastPickBlockTime();
        return true;
    }

}
