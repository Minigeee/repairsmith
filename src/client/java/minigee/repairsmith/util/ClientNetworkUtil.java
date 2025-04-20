package minigee.repairsmith.util;

import minigee.repairsmith.RepairsmithClient;
import minigee.repairsmith.network.RepairItemsPacketPayload;
import minigee.repairsmith.network.SyncTradeOffersPayload;
import minigee.repairsmith.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.village.TradeOfferList;

public class ClientNetworkUtil {

	public static void init() {

		ClientPlayNetworking.registerGlobalReceiver(SyncTradeOffersPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				ScreenHandler screenHandler = context.client().player.currentScreenHandler;

				if (payload.syncId() == screenHandler.syncId && screenHandler instanceof RepairScreenHandler) {
					RepairScreenHandler repairScreenHandler = (RepairScreenHandler) screenHandler;
					repairScreenHandler.setOffers(payload.tradeOfferList());
					repairScreenHandler.setLevelProgress(payload.level());
				}
			});
		});
	}

}
