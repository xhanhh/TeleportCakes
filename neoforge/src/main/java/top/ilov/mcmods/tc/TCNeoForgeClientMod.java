package top.ilov.mcmods.tc;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.ilov.mcmods.tc.integration.clothconfig.ClothConfig;
import top.ilov.mcmods.tc.platform.Services;

import java.util.function.Supplier;

@Mod(value = TeleportCakesMod.MOD_ID, dist = Dist.CLIENT)
public class TCNeoForgeClientMod {

    public TCNeoForgeClientMod(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (Supplier<IConfigScreenFactory>) () -> (_, parent) -> ClothConfig.genConfigScreen(parent)
            );
        }
    }

}
