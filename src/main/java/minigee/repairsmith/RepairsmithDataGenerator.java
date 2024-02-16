package minigee.repairsmith;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class RepairsmithDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(PoiTagProvider::new);
	}

	private static class PoiTagProvider extends TagProvider<PointOfInterestType> {

		public PoiTagProvider(DataOutput output, CompletableFuture<WrapperLookup> registryLookupFuture) {
			super(output, RegistryKeys.POINT_OF_INTEREST_TYPE, registryLookupFuture);
		}

		@Override
		protected void configure(WrapperLookup var1) {
			this.getOrCreateTagBuilder(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)
					.addOptional(new Identifier(Repairsmith.MOD_ID, "repairsmith_poi"));
		}

	}
}
