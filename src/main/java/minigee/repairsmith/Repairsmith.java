package minigee.repairsmith;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import minigee.repairsmith.screen.RepairScreenHandler;
import minigee.repairsmith.util.NetworkUtil;

public class Repairsmith implements ModInitializer {
	public static final String MOD_ID = "repairsmith";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Config */
	public static Config CONFIG = new Config(30, 0.7f, 5, 50, 20, 0.6f);

	/** Poi registry key */
	public static final RegistryKey<PointOfInterestType> REPAIRSMITH_POI_KEY = RegistryKey
			.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(MOD_ID, "repairsmith_poi"));
	/** Repairsmith profession */
	public static final VillagerProfession REPAIRSMITH = new VillagerProfession("repairsmith",
			entry -> entry.matchesKey(REPAIRSMITH_POI_KEY), entry -> entry.matchesKey(REPAIRSMITH_POI_KEY),
			ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);

	public static final ScreenHandlerType<RepairScreenHandler> REPAIR_SCREEN_HANDLER = new ScreenHandlerType<>(
			RepairScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

	@Override
	public void onInitialize() {
		// Load config
		Config.setup();

		// Villager
		PointOfInterestHelper.register(new Identifier(MOD_ID, "repairsmith_poi"), 1, 1, Blocks.ANVIL,
				Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL);
		Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(MOD_ID, "repairsmith"), REPAIRSMITH);

		// Screen handler
		Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "repairsmith"), REPAIR_SCREEN_HANDLER);

		// Dummy offer so that repairsmith has something to offer
		TradeOfferHelper.registerVillagerOffers(REPAIRSMITH, 1, (factories) -> {
			factories.add((entity, random) -> new TradeOffer(new ItemStack(Blocks.STONE, 100), new ItemStack(Blocks.STONE, 100),
					CONFIG.maxOffers(), 1, 0.1f));
		});

		NetworkUtil.init();
	}
}