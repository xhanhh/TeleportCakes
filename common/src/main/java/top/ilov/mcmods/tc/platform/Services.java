package top.ilov.mcmods.tc.platform;

import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IPlatformHelper;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegisterHelper REGISTER = load(IRegisterHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        TeleportCakesMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}