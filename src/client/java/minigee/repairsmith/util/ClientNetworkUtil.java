package minigee.repairsmith.util;

import minigee.repairsmith.network.SyncTradeOffersPayload;
import minigee.repairsmith.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.screen.ScreenHandler;

public class ClientNetworkUtil {

	public static void init() {

		ClientPlayNetworking.registerGlobalReceiver(SyncTradeOffersPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				ScreenHandler screenHandler = context.client().player.currentScreenHandler;

				if (payload.syncId() == screenHandler.syncId && screenHandler instanceof RepairScreenHandler) {
					RepairScreenHandler repairScreenHandler = (RepairScreenHandler) screenHandler;
					repairScreenHandler.setOffers(payload.tradeOfferList());
				}
			});
		});
	}

}
