package minigee.repairsmith.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minigee.repairsmith.Repairsmith;
import minigee.repairsmith.screen.RepairScreenHandler;
import minigee.repairsmith.util.NetworkUtil;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
	@Shadow
	@Final
	public VillagerData getVillagerData() {
		return new VillagerData(null, null, DEFAULT_MIN_FREEZE_DAMAGE_TICKS);
	}

	@Shadow
	@Final
	private void prepareOffersFor(PlayerEntity player) {
	}

	public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
		super(entityType, world);
	}

	/** Shows repair screen if villager is a repairsmith */
	@Inject(at = @At("HEAD"), method = "beginTradeWith", cancellable = true)
	private void beginTradeWith(PlayerEntity customer, CallbackInfo info) {
		// Check if profession is repairsmith
		if (this.getVillagerData().getProfession() == Repairsmith.REPAIRSMITH) {
			this.prepareOffersFor(customer);
			this.setCustomer(customer);

			// Open screen
			OptionalInt optionalInt = customer
					.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> {
						return new RepairScreenHandler(syncId, playerInventory, this);
					}, this.getDisplayName()));

			// Send trade offers (only 1 that contains uses, villager xp, price multiplier)
			TradeOfferList tradeOfferList;
			if (optionalInt.isPresent() && !(tradeOfferList = this.getOffers()).isEmpty()) {
				NetworkUtil.syncTradeOffers((ServerPlayerEntity) customer, optionalInt.getAsInt(), tradeOfferList);
			}

			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "afterUsing", cancellable = true)
	private void afterUsing(TradeOffer offer, CallbackInfo info) {
		if (this.getVillagerData().getProfession() == Repairsmith.REPAIRSMITH) {
			info.cancel();
		}
	}
}