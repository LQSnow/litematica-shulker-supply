package top.lqsnow.lss;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lqsnow.lss.config.ConfigManager;
import top.lqsnow.lss.net.HandshakeC2S;
import top.lqsnow.lss.net.HandshakeS2C;
import top.lqsnow.lss.net.SwapFromShulkerC2S;

/**
 * Server entry point of the mod. Registers networking payloads, handles
 * shulker-to-hotbar swapping and loads the configuration on startup.
 */
public class LitematicaShulkerSupply implements ModInitializer {
    public static final String MOD_ID = "litematica-shulker-supply";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    /**
     * Initialize networking payloads and read configuration.
     */
    @Override
    public void onInitialize() {
        // Register custom payload types
        PayloadTypeRegistry.playC2S().register(HandshakeC2S.ID, HandshakeC2S.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakeS2C.ID, HandshakeS2C.CODEC);
        PayloadTypeRegistry.playC2S().register(SwapFromShulkerC2S.ID, SwapFromShulkerC2S.CODEC);

        // Handshake: simply reply OK
        ServerPlayNetworking.registerGlobalReceiver(HandshakeC2S.ID, (payload, context) ->
                ServerPlayNetworking.send(context.player(), new HandshakeS2C(true))
        );

        // Swap a slot in shulker box with a hotbar slot
        ServerPlayNetworking.registerGlobalReceiver(SwapFromShulkerC2S.ID, (payload, context) -> {
            handleSwapFromShulker(
                    context.player(),
                    payload.boxContainerSlotId(),
                    payload.innerIndex(),
                    payload.destHotbarSlot()
            );
        });


    }

    /**
     * Execute the swap: move one slot from shulker box to a hotbar slot and put
     * the previous hotbar item back into the shulker. Any invalid parameter will
     * simply abort the operation silently.
     */
    private static void handleSwapFromShulker(ServerPlayerEntity player, int boxContainerSlotId, int innerIndex, int destHotbar) {
        if (destHotbar < 0 || destHotbar > 8) return;
        if (player.isCreative()) return; // Optional: match client behavior

        PlayerScreenHandler handler = player.playerScreenHandler;
        if (boxContainerSlotId < 0 || boxContainerSlotId >= handler.slots.size()) return;

        Slot boxSlot = handler.slots.get(boxContainerSlotId);
        if (boxSlot.inventory != player.getInventory()) return;

        ItemStack boxStack = boxSlot.getStack();
        if (!(boxStack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) return;

        ContainerComponent container = boxStack.getComponents().get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
        container.copyTo(list);
        if (innerIndex < 0 || innerIndex >= list.size()) return;

        ItemStack fromBox = list.get(innerIndex).copy();
        if (fromBox.isEmpty()) return;

        PlayerInventory inv = player.getInventory();
        ItemStack destExisting = inv.getStack(destHotbar).copy();

        if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi2 && bi2.getBlock() instanceof ShulkerBoxBlock) {
            return;
        }

        inv.setStack(destHotbar, fromBox);
        list.set(innerIndex, destExisting);
        boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
        boxSlot.setStack(boxStack);

        inv.setSelectedSlot(destHotbar);
        inv.markDirty();
        handler.sendContentUpdates();
    }

}

