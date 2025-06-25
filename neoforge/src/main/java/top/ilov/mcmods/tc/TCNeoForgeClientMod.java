package top.ilov.mcmods.tc;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.ilov.mcmods.tc.integration.clothconfig.ClothConfig;

import java.util.function.Supplier;

@Mod(value = TeleportCakesMod.MOD_ID, dist = Dist.CLIENT)
public class TCNeoForgeClientMod {

    public TCNeoForgeClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (Supplier<IConfigScreenFactory>) () -> (mod, parent) -> ClothConfig.genConfigScreen(parent)
        );
    }

}
