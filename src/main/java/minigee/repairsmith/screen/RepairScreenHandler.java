package minigee.repairsmith.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import minigee.repairsmith.Repairsmith;
import minigee.repairsmith.util.InventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.SimpleMerchant;
import net.minecraft.village.TradeOfferList;

public class RepairScreenHandler extends ScreenHandler {
	public final Merchant merchant;
	public final SimpleInventory inventory;
	private final PlayerInventory playerInv;

	/** Cost of item repair in emeralds */
	private int cost = 0;

	public RepairScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
	}

	public RepairScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
		super(Repairsmith.REPAIR_SCREEN_HANDLER, syncId);

		this.merchant = merchant;
		this.inventory = new SimpleInventory(10);
		this.playerInv = playerInventory;
		this.inventory.onOpen(playerInventory.player);

		// Update repair cost on inventory change
		this.inventory.addListener((inv) -> {
			// Calculate price multiplier from other effects
			var offer = this.merchant.getOffers().size() > 0 ? this.merchant.getOffers().get(0) : null;
			double priceMultiplier = offer == null ? 1.0 : (1.0 + (double) offer.getSpecialPrice() / 100.0);

			// Calc cost
			int totalCost = 0;
			for (int i = 0; i < inv.size(); ++i) {
				final var stack = inv.getStack(i);

				double cost = (double) Repairsmith.CONFIG.costPerDurability() * stack.getDamage();
				totalCost += (int) Math.ceil(cost * priceMultiplier);
			}

			this.cost = totalCost;
		});

		int m;
		int l;
		// Repairsmith inventory
		for (m = 0; m < 2; ++m) {
			for (l = 0; l < 5; ++l) {
				this.addSlot(new RepairSlot(inventory, l + m * 5, 8 + l * 18, 29 + m * 18));
			}
		}
		// The player inventory
		for (m = 0; m < 3; ++m) {
			for (l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
			}
		}
		// The player Hotbar
		for (m = 0; m < 9; ++m) {
			this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
		}

	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.merchant.getCustomer() == player;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();
			if (invSlot < this.inventory.size()) {
				if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (originalStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return newStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);

		this.merchant.setCustomer(null);
		this.dropInventory(player, this.inventory);
	}

	/** Used to sync trade offers */
	public void setOffers(TradeOfferList offers) {
		this.merchant.setOffersFromServer(offers);
	}

	/**
	 * Get repair cost of inventory items in emeralds
	 * 
	 * @return The cost to repair in emeralds
	 */
	public int getRepairCost() {
		return this.cost;
	}

	/**
	 * Checks if villager has more repair trade offers
	 * 
	 * @return True if more offers available
	 */
	public boolean hasMoreOffers() {
		final var offer = this.merchant.getOffers().get(0);
		return offer.getUses() < offer.getMaxUses();
	}

	/**
	 * Performs repair action on all damaged items in repairsmith inventory.
	 * Must be called from server to persist changes.
	 */
	public void repairAll() {
		// Check if player can afford
		if (!InventoryUtil.canAfford(this.playerInv, this.cost))
			return;

		// Reset all damage
		int damageRepaired = 0;
		for (final var stack : this.inventory.heldStacks) {
			damageRepaired += stack.getDamage();
			stack.setDamage(0);
		}

		ModComponents.CURRENCY.get(this.playerInv.player).modify(-this.cost);

		// Play sound
		this.playerInv.player.playSound(SoundEvents.BLOCK_ANVIL_USE);

		// Use special price as a field to transmit durability repaired
		var offer = this.merchant.getOffers().get(0);
		int oldSpecialPrice = offer.getSpecialPrice();
		offer.setSpecialPrice(damageRepaired);

		// Villager offer
		this.merchant.trade(offer);
		this.playerInv.player.incrementStat(Stats.TRADED_WITH_VILLAGER);

		// Reset special price
		offer.setSpecialPrice(oldSpecialPrice);
	}

	/** Slot class for repair screen */
	private class RepairSlot extends Slot {

		public RepairSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return stack.isDamageable();
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}

	}

}
