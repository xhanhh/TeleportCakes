package top.ilov.mcmods.tc;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

public class TeleportCakesMod implements ModInitializer {

	public static final String MOD_ID = "teleportcakes";

    public static final Logger LOGGER = LoggerFactory.getLogger("TeleportCakes");

	public static CakeConfig CONFIG = new CakeConfig();

	public static ResourceLocation rl(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	@Override
	public void onInitialize() {

		CONFIG = CakeConfig.loadConfig();

		BlocksRegistry.registerBlocks();
		ItemGroup.registerItemGroup();

		LOGGER.info("Hello Cakes!");

	}
}