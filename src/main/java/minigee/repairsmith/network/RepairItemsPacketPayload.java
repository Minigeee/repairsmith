package minigee.repairsmith.network;

import minigee.repairsmith.util.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.village.TradeOfferList;

public record RepairItemsPacketPayload(int syncId) implements CustomPayload {
    public static final CustomPayload.Id<RepairItemsPacketPayload> ID = new CustomPayload.Id<>(NetworkUtil.REPAIR_ITEMS_PACKET);

    public static final PacketCodec<RegistryByteBuf, RepairItemsPacketPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RepairItemsPacketPayload::syncId,
            RepairItemsPacketPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
