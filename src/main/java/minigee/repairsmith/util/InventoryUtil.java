package minigee.repairsmith.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

public class InventoryUtil {

	public static boolean canAfford(PlayerInventory playerInventory, int cost) {
		int budget = 0;
		for (final var stack : playerInventory.offHand) {
			if (stack.getItem() == Items.EMERALD)
				budget += stack.getCount();
		}
		for (final var stack : playerInventory.main) {
			if (stack.getItem() == Items.EMERALD)
				budget += stack.getCount();
		}

		return budget >= cost;
	}
}
