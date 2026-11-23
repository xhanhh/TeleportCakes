package top.ilov.mcmods.tc.platform;

import net.fabricmc.loader.api.FabricLoader;
import top.ilov.mcmods.tc.network.AggregateCupcakePayload;
import top.ilov.mcmods.tc.network.FabricPayloads;
import top.ilov.mcmods.tc.platform.services.IPlatformHelper;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void sendAggregateCupcakeSelection(AggregateCupcakePayload payload) {
        FabricPayloads.send(payload);
    }

}
