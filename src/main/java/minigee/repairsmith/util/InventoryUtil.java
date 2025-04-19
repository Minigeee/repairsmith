package minigee.repairsmith.util;

import com.glisco.numismaticoverhaul.ModComponents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;

public class InventoryUtil {

	public static boolean canAfford(PlayerInventory playerInventory, int cost) {
		return ModComponents.CURRENCY.get(playerInventory.player).getValue() >= cost;
	}
}
