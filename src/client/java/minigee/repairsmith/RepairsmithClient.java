package minigee.repairsmith;

import minigee.repairsmith.util.ClientNetworkUtil;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RepairsmithClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientNetworkUtil.init();

		HandledScreens.register(Repairsmith.REPAIR_SCREEN_HANDLER, RepairScreen::new);
	}

}