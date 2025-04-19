package minigee.repairsmith;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;

public record Config(
		/** The amount of durability that 1 emerald can repair */
		int costPerDurability,
		/** Max number of repair trade offers per cycle */
		int maxOffers) {

	/** Village structures config */
	public static final StructurePoolConfig STRUCTURES = new StructurePoolConfig();

	/** Config file path */
	public static final String CONFIG_PATH = "config" + File.separator + Repairsmith.MOD_ID + ".json";

	/** Gson parser/writer */
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	/**
	 * Read or create config data
	 */
	public static void setup() {
		try {
			if (Files.exists(Path.of(CONFIG_PATH))) {
				// Read and parse config
				FileReader reader = new FileReader(CONFIG_PATH);
				Repairsmith.CONFIG = gson.fromJson(reader, Config.class);
				reader.close();
			} else {
				// Create config dir if not exist
				if (Files.notExists(Path.of("config")))
					Files.createDirectory(Path.of("config"));

				// Write current config data
				FileWriter writer = new FileWriter(CONFIG_PATH);
				writer.write(gson.toJson(Repairsmith.CONFIG));
				writer.flush();
				writer.close();
			}

			int weight = 10;
			int limit = 2;
			STRUCTURES.entries = new ArrayList<>(List.of(
					new StructurePoolConfig.Entry("minecraft:village/desert/houses", new ArrayList<>(Arrays.asList(
							new StructurePoolConfig.Entry.Structure("repairsmith:village/desert/desert_repairsmith",
									weight, limit)))),
					new StructurePoolConfig.Entry("minecraft:village/plains/houses", new ArrayList<>(Arrays.asList(
							new StructurePoolConfig.Entry.Structure("repairsmith:village/plains/plains_repairsmith",
									weight, limit)))),
					new StructurePoolConfig.Entry("minecraft:village/savanna/houses", new ArrayList<>(Arrays.asList(
							new StructurePoolConfig.Entry.Structure("repairsmith:village/savanna/savanna_repairsmith",
									weight, limit)))),
					new StructurePoolConfig.Entry("minecraft:village/snowy/houses", new ArrayList<>(Arrays.asList(
							new StructurePoolConfig.Entry.Structure("repairsmith:village/snowy/snowy_repairsmith",
									weight, limit)))),
					new StructurePoolConfig.Entry("minecraft:village/taiga/houses", new ArrayList<>(Arrays.asList(
							new StructurePoolConfig.Entry.Structure("repairsmith:village/taiga/taiga_repairsmith",
									weight, limit))))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
