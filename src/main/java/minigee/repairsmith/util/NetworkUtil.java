package minigee.repairsmith.util;

import minigee.repairsmith.Repairsmith;
import minigee.repairsmith.network.RepairItemsPacketPayload;
import minigee.repairsmith.network.SyncTradeOffersPayload;
import minigee.repairsmith.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOfferList;

public class NetworkUtil {

	public static final Identifier SYNC_TRADE_OFFERS = Identifier.of(Repairsmith.MOD_ID, "sync_trade_offers");
	public static final Identifier REPAIR_ITEMS_PACKET = Identifier.of(Repairsmith.MOD_ID, "repair_items");

	public static void init() {

		PayloadTypeRegistry.playS2C().register(SyncTradeOffersPayload.ID, SyncTradeOffersPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(RepairItemsPacketPayload.ID, RepairItemsPacketPayload.CODEC);


		ServerPlayNetworking.registerGlobalReceiver(RepairItemsPacketPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();
				ScreenHandler screenHandler = context.player().currentScreenHandler;
				int syncId = payload.syncId();
				if (syncId == screenHandler.syncId && screenHandler instanceof RepairScreenHandler) {
					// Repair items
					RepairScreenHandler repairScreenHandler = (RepairScreenHandler) screenHandler;
					repairScreenHandler.repairAll();

					// Sync new trade offers
					VillagerEntity villager = (VillagerEntity)repairScreenHandler.merchant;

					// Prepare the payload
					SyncTradeOffersPayload SyncTradeOffersPayload = new SyncTradeOffersPayload(villager.getOffers(), syncId);
					ServerPlayNetworking.send(context.player(), SyncTradeOffersPayload);
				}
			});
		});
	}

	/**
	 * Send packet to sync trade offers for repair screen,
	 * so that user can see correct offers data.
	 * 
	 * @param player The player to send to
	 * @param syncId The sync id of the handled screen
	 * @param offers The list of offers to sync
	 */
	public static void syncTradeOffers(ServerPlayerEntity player, int syncId, TradeOfferList offers) {
		SyncTradeOffersPayload SyncTradeOffersPayload = new SyncTradeOffersPayload(offers, syncId);
		// Send message
		ServerPlayNetworking.send(player, SyncTradeOffersPayload);
	}

}
