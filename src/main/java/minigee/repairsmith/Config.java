package minigee.repairsmith;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public record Config(
		/** The amount of durability that 1 emerald can repair */
		int durabilityPerEmerald,
		/** The exponent value that gets applied to the emerald cost */
		float costExp,
		/** Max number of repair trade offers per cycle */
		int maxOffers,
		/** Amount of durability repair needed to reward 1 player xp */
		int durabilityPerPlayerXp,
		/** Amount of durability repair needed to reward 1 villager xp */
		int durabilityPerVillagerXp,
		/** The exponent value that gets applied to xp reward values */
		float xpExp) {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
