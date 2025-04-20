package minigee.repairsmith.network;

import minigee.repairsmith.util.NetworkUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SyncTradeOffersPayload(TradeOfferList tradeOfferList, int syncId, int level) implements CustomPayload {
    public static final CustomPayload.Id<SyncTradeOffersPayload> ID = new CustomPayload.Id<>(NetworkUtil.SYNC_TRADE_OFFERS);
    //public static final PacketCodec<RegistryByteBuf, SyncTradeOffersPayload> CODEC = PacketCodec.tuple(TradeOfferList.PACKET_CODEC, SyncTradeOffersPayload::tradeOfferList, SyncTradeOffersPayload::new);
    // should you need to send more data, add the appropriate record parameters and change your codec:
    public static final PacketCodec<RegistryByteBuf, SyncTradeOffersPayload> CODEC = PacketCodec.tuple(
        TradeOfferList.PACKET_CODEC,
        SyncTradeOffersPayload::tradeOfferList,
        PacketCodecs.INTEGER, SyncTradeOffersPayload::syncId,
        PacketCodecs.INTEGER, SyncTradeOffersPayload::level,
        SyncTradeOffersPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
