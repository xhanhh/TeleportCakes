package top.ilov.mcmods.tc;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import top.ilov.mcmods.tc.network.ForgePayloads;
import top.ilov.mcmods.tc.platform.ForgeRegisterHelper;

@Mod(TeleportCakesMod.MOD_ID)
public class TCForgeMod {

    public TCForgeMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TeleportCakesMod.init();
        ForgePayloads.register();
        ForgeRegisterHelper.BLOCKS.register(eventBus);
        ForgeRegisterHelper.ITEMS.register(eventBus);
        ForgeItemGroup.register(eventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            TCForgeClientMod.registerConfigScreen();
        }

    }

}