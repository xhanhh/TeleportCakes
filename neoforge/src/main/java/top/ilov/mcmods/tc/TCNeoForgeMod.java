package top.ilov.mcmods.tc;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import top.ilov.mcmods.tc.network.NeoForgePayloads;
import top.ilov.mcmods.tc.platform.NeoForgeRegisterHelper;

@Mod(TeleportCakesMod.MOD_ID)
public class TCNeoForgeMod {

    public TCNeoForgeMod(IEventBus eventBus) {

        TeleportCakesMod.init();
        eventBus.addListener(NeoForgePayloads::registerPayloads);
        NeoForgeRegisterHelper.BLOCKS.register(eventBus);
        NeoForgeRegisterHelper.ITEMS.register(eventBus);
        NeoForgeItemGroup.register(eventBus);

    }

}