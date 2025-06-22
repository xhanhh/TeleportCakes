package top.ilov.mcmods.tc.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import top.ilov.mcmods.tc.integration.clothconfig.ClothConfig;
import top.ilov.mcmods.tc.utils.CommonUtils;

public class CakeMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        if (CommonUtils.isModLoaded("cloth-config2")) {
            return ClothConfig::genConfigScreen;
        } else {
            return parent -> null;
        }
    }

}
