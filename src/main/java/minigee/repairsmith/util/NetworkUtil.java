package minigee.repairsmith.util;

import minigee.repairsmith.Repairsmith;
import minigee.repairsmith.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOfferList;

public class NetworkUtil {

	public static final Identifier SYNC_TRADE_OFFERS = new Identifier(Repairsmith.MOD_ID, "sync_trade_offers");
	public static final Identifier REPAIR_ITEMS_PACKET = new Identifier(Repairsmith.MOD_ID, "repair_items");

	public static void init() {

		// Repair items action
		ServerPlayNetworking.registerGlobalReceiver(REPAIR_ITEMS_PACKET,
				(server, player, handler, buf, responseSender) -> {
					int syncId = buf.readVarInt();
					ScreenHandler screenHandler = player.currentScreenHandler;

					if (syncId == screenHandler.syncId && screenHandler instanceof RepairScreenHandler) {
						// Repair items
						RepairScreenHandler repairScreenHandler = (RepairScreenHandler) screenHandler;
						repairScreenHandler.repairAll();

						// Sync new trade offers
						VillagerEntity villager = (VillagerEntity)repairScreenHandler.merchant;
						NetworkUtil.syncTradeOffers(player, syncId, villager.getOffers(), villager.getVillagerData().getLevel());
					}
				});
	}

	/**
	 * Send packet to sync trade offers for repair screen,
	 * so that user can see correct offers data.
	 * 
	 * @param player The player to send to
	 * @param syncId The sync id of the handled screen
	 * @param offers The list of offers to sync
	 * @param level The villager level
	 */
	public static void syncTradeOffers(ServerPlayerEntity player, int syncId, TradeOfferList offers, int level) {
		// Create packet
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(syncId);
		offers.toPacket(buf);
		buf.writeInt(level);

		// Send message
		ServerPlayNetworking.send(player, NetworkUtil.SYNC_TRADE_OFFERS, buf);
	}

}
