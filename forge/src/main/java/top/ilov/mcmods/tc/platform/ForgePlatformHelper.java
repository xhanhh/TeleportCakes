package top.ilov.mcmods.tc.platform;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import top.ilov.mcmods.tc.network.AggregateCupcakePayload;
import top.ilov.mcmods.tc.network.ForgePayloads;
import top.ilov.mcmods.tc.platform.services.IPlatformHelper;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public void sendAggregateCupcakeSelection(AggregateCupcakePayload payload) {
        ForgePayloads.send(payload);
    }
}