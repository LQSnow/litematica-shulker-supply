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
 * <p>
 * 模组服务端入口，负责注册网络负载、处理潜影盒与快捷栏的互换逻辑，并在启动时加载配置。
 */
public class LitematicaShulkerSupply implements ModInitializer {
    public static final String MOD_ID = "litematica-shulker-supply";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigManager CONFIG = new ConfigManager(
            FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json")
    );

    /**
     * Initialize networking payloads and read configuration.
     * 初始化：注册网络负载与处理器，并读取配置。
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
            ServerPlayerEntity player = context.player();
            context.player().getServer().execute(() -> handleSwapFromShulker(
                    player,
                    payload.boxContainerSlotId(),
                    payload.innerIndex(),
                    payload.destHotbarSlot()
            ));
        });

        try {
            CONFIG.load();
        } catch (Exception e) {
            LOGGER.warn("[{}] Failed to pre-load config: {}", MOD_ID, e.getMessage());
        }
    }

    /**
     * Execute the swap: move one slot from shulker box to a hotbar slot and put
     * the previous hotbar item back into the shulker. Any invalid parameter will
     * simply abort the operation silently.
     * <p>
     * 交换执行：潜影盒内某格与玩家快捷栏某槽整格互换。如参数非法则直接返回。
     */
    private static void handleSwapFromShulker(ServerPlayerEntity player, int boxContainerSlotId, int innerIndex, int destHotbar) {
        if (destHotbar < 0 || destHotbar > 8) return;

        PlayerScreenHandler handler = player.playerScreenHandler;
        if (boxContainerSlotId < 0 || boxContainerSlotId >= handler.slots.size()) return;

        Slot boxSlot = handler.slots.get(boxContainerSlotId);
        ItemStack boxStack = boxSlot.getStack();
        if (!(boxStack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return; // Not a shulker box
        }

        ContainerComponent container = boxStack.getComponents().get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
        container.copyTo(list);
        if (innerIndex < 0 || innerIndex >= list.size()) return;

        ItemStack fromBox = list.get(innerIndex).copy();
        if (fromBox.isEmpty()) return;

        // ✅ 关键改动：用玩家“逻辑热栏索引”直接访问/写入，而不是 36 + hotbar 的容器索引
        PlayerInventory inv = player.getInventory();
        ItemStack destExisting = inv.getStack(destHotbar).copy();

        // Prevent "shulker-in-shulker"
        if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi2 && bi2.getBlock() instanceof ShulkerBoxBlock) {
            return;
        }

        // 执行互换：热栏 <- 盒内；盒内 <- 原热栏
        inv.setStack(destHotbar, fromBox);
        list.set(innerIndex, destExisting);
        boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
        boxSlot.setStack(boxStack);

        // 选择该热栏（与客户端预测一致）
        inv.setSelectedSlot(destHotbar);

        // 标脏 + 同步
        inv.markDirty();
        handler.sendContentUpdates();
    }
}

