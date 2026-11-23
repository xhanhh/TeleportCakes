package top.ilov.mcmods.tc;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import top.ilov.mcmods.tc.integration.clothconfig.ClothConfig;
import top.ilov.mcmods.tc.platform.Services;

public class TCForgeClientMod {

    public static void registerConfigScreen() {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(ClothConfig::genConfigScreen));
        }
    }

}
