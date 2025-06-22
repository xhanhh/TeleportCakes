package top.ilov.mcmods.tc.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CommonUtils {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

}
