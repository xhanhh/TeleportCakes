package top.ilov.mcmods.tc;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

public class TeleportCakesMod {

	public static final String MOD_ID = "teleportcakes";

    public static final Logger LOGGER = LoggerFactory.getLogger("TeleportCakes");

	public static CakeConfig CONFIG = new CakeConfig();

	public static Identifier rl(String id) {
		return Identifier.fromNamespaceAndPath(MOD_ID, id);
	}

	public static void init() {

		CONFIG = CakeConfig.loadConfig();
		BlocksRegistry.registerBlocks();
		LOGGER.info("Hello Cakes!");

	}

}