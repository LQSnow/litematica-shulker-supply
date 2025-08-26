package top.lqsnow.lss.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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
 */
@Environment(EnvType.CLIENT)
public class ShulkerSwapClientLogic {


    /**
     * Attempt to extract a matching item from a shulker box and swap it with
     * the specified hotbar slot. Performs client-side prediction and sends a
     * confirmation packet to the server when successful.
     *
     * @param mc       current client instance
     * @param required required item stack
     * @return whether the swap completed successfully
     */
    public static boolean tryExtractFromShulkerAndSwap(MinecraftClient mc, ItemStack required) {
        PlayerEntity player = mc.player;
        if (player == null || required == null || required.isEmpty()) return false;

        PlayerScreenHandler handler = player.playerScreenHandler;
        PlayerInventory inv = player.getInventory();

        // 1) Find the first shulker containing the required item: (container slot ID, inner index)
        ShulkerHit hit = findShulkerWithRequired(handler, required);
        if (hit == null) return false;

        // 2) Pick a target hotbar slot (follow pick block rules and exclude shulker slots)
        int hotbarSlot = pickHotbarTarget(inv, player);
        if (hotbarSlot < 0) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return false;
        }

        ItemStack fromBox = hit.innerCopy();
        ItemStack destExisting = inv.getStack(hotbarSlot).copy();

        // Disallow shulker boxes inside shulker boxes
        if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, "litematica.message.warn.pickblock.no_suitable_slot_found");
            return false;
        }

        // 3) Client-side prediction: swap innerIndex with hotbarSlot
        inv.getMainStacks().set(hotbarSlot, fromBox.copy());
        inv.setSelectedSlot(hotbarSlot);
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
        }

        DefaultedList<ItemStack> list = hit.list;
        list.set(hit.innerIndex, destExisting.copy());
        ItemStack boxStack = hit.boxSlot.getStack().copy();
        boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
        hit.boxSlot.setStack(boxStack);

        // 4) Send packet for server confirmation
        ClientPlayNetworking.send(new SwapFromShulkerC2S(hit.containerSlotId, hit.innerIndex, hotbarSlot));

        // Record cooldown (matches pick block rate limit)
        fi.dy.masa.litematica.util.WorldUtils.setEasyPlaceLastPickBlockTime();
        return true;
    }

    /**
     * Search the current container for the first shulker box containing the
     * required item.
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
     * Find the index of the first item matching the required stack within a
     * shulker box's contents.
     */
    private static int firstIndexMatch(DefaultedList<ItemStack> list, ItemStack required) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack it = list.get(i);
            if (!it.isEmpty() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreNbt(it, required)) {
                return i; // first matching slot
            }
        }
        return -1;
    }

    /**
     * Select a target hotbar slot that can be used for pick block and is not a
     * shulker box.
     */
    private static int pickHotbarTarget(PlayerInventory inv, PlayerEntity player) {
        int slot = AccessorLitematicaInventoryUtils.lss$invokeGetEmptyPickBlockableHotbarSlot(inv);
        if (slot == -1) {
            slot = AccessorLitematicaInventoryUtils.lss$invokeGetPickBlockTargetSlot(player);
        }
        if (slot == -1) return -1;

        ItemStack exist = inv.getStack(slot);
        if (!exist.isEmpty() && exist.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            return -1; // new constraint: target slot cannot hold a shulker box
        }
        return slot;
    }

    /**
     * Record information about a shulker box that matched the required item.
     */
    private record ShulkerHit(int containerSlotId, Slot boxSlot, DefaultedList<ItemStack> list, int innerIndex) {
        ItemStack innerCopy() { return list.get(innerIndex).copy(); }
    }

}
