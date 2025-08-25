package top.lqsnow.lss;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lqsnow.lss.config.ConfigManager;
import top.lqsnow.lss.net.HandshakeC2S;
import top.lqsnow.lss.net.HandshakeS2C;
import top.lqsnow.lss.net.SwapFromShulkerC2S;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

/**
 * Mod 的服务端入口。负责注册网络负载、处理潜影盒与快捷栏的互换逻辑，并在启动时加载配置。
 */
public class LitematicaShulkerSupply implements ModInitializer {
        public static final String MOD_ID = "litematica-shulker-supply";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        public static final ConfigManager CONFIG = new ConfigManager(
                        FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json")
        );

        /**
         * 初始化：注册网络负载、握手与物品互换的处理器，并读取配置。
         */
        @Override
        public void onInitialize() {
                // 注册自定义负载类型
                PayloadTypeRegistry.playC2S().register(HandshakeC2S.ID, HandshakeC2S.CODEC);
                PayloadTypeRegistry.playS2C().register(HandshakeS2C.ID, HandshakeS2C.CODEC);
                PayloadTypeRegistry.playC2S().register(SwapFromShulkerC2S.ID, SwapFromShulkerC2S.CODEC);

		// 握手
		ServerPlayNetworking.registerGlobalReceiver(HandshakeC2S.ID, (payload, context) -> {
			// 直接回个 OK
			ServerPlayNetworking.send(context.player(), new HandshakeS2C(true));
		});

		// 交换执行：潜影盒内某格 与 玩家快捷栏某槽 整格互换
		ServerPlayNetworking.registerGlobalReceiver(SwapFromShulkerC2S.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			context.player().getServer().execute(() -> {
				try {
					int boxContainerSlotId = payload.boxContainerSlotId();
					int innerIndex = payload.innerIndex();
					int destHotbar = payload.destHotbarSlot(); // 0..8

					if (destHotbar < 0 || destHotbar > 8) return;

					PlayerScreenHandler handler = player.playerScreenHandler;
					if (boxContainerSlotId < 0 || boxContainerSlotId >= handler.slots.size()) return;

					Slot boxSlot = handler.slots.get(boxContainerSlotId);
					ItemStack boxStack = boxSlot.getStack();
					if (!(boxStack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
						return; // 不是潜影盒
					}

					ContainerComponent container = boxStack.getComponents().get(DataComponentTypes.CONTAINER);
					if (container == null) return;

					DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
					container.copyTo(list);
					if (innerIndex < 0 || innerIndex >= list.size()) return;

					ItemStack fromBox = list.get(innerIndex).copy();
					if (fromBox.isEmpty()) return;

					int destContainerSlotId = 36 + destHotbar; // 服务器容器编号
					if (destContainerSlotId < 0 || destContainerSlotId >= handler.slots.size()) return;

					Slot destSlot = handler.slots.get(destContainerSlotId);
					ItemStack destExisting = destSlot.getStack().copy();

					// 禁止“潜影盒进潜影盒”
					if (!destExisting.isEmpty() && destExisting.getItem() instanceof BlockItem bi2 && bi2.getBlock() instanceof ShulkerBoxBlock) {
						return;
					}

					// 执行互换
					destSlot.setStack(fromBox);
					list.set(innerIndex, destExisting);
					boxStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));
					boxSlot.setStack(boxStack);

					// 选中该快捷栏槽（与客户端预测一致）
					player.getInventory().setSelectedSlot(destHotbar);

					// 同步
					handler.sendContentUpdates();
				} catch (Exception ignored) {}
			});
		});

		try {
			CONFIG.load();
		} catch (Exception e) {
			LOGGER.warn("[{}] Failed to pre-load config: {}", MOD_ID, e.getMessage());
		}
	}
}
