package top.ilov.mcmods.tc;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.items.ItemRegistry;

public class TeleportCakesMod {

	public static final String MOD_ID = "teleportcakes";

    public static final Logger LOGGER = LoggerFactory.getLogger("TeleportCakes");

	public static CakeConfig CONFIG = new CakeConfig();

	public static ResourceLocation rl(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}

	public static void init() {

		CONFIG = CakeConfig.loadConfig();
		BlocksRegistry.registerBlocks();
		ItemRegistry.registerItems();
		LOGGER.info("Hello Cakes!");

	}

}