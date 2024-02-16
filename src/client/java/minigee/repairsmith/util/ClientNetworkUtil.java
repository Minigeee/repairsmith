package minigee.repairsmith.util;

import minigee.repairsmith.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.village.TradeOfferList;

public class ClientNetworkUtil {

	public static void init() {

		// Sync client's trade offers
		ClientPlayNetworking.registerGlobalReceiver(NetworkUtil.SYNC_TRADE_OFFERS,
				(client, handler, buf, responseSender) -> {
					int syncId = buf.readVarInt();
					TradeOfferList offers = TradeOfferList.fromPacket(buf);
					int level = buf.readInt();

					client.execute(() -> {
						ScreenHandler screenHandler = client.player.currentScreenHandler;

						if (syncId == screenHandler.syncId && screenHandler instanceof RepairScreenHandler) {
							RepairScreenHandler repairScreenHandler = (RepairScreenHandler) screenHandler;
							repairScreenHandler.setOffers(offers);
							repairScreenHandler.setLevelProgress(level);
						}
					});
				});
	}

}
