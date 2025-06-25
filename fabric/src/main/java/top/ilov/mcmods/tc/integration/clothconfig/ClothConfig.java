package top.ilov.mcmods.tc.integration.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.ilov.mcmods.tc.CakeConfig;
import top.ilov.mcmods.tc.TeleportCakesMod;

public class ClothConfig {

    public static Screen genConfigScreen(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable("config.teleportcakes.title"))
                .setParentScreen(parent)
                .setSavingRunnable(() -> CakeConfig.write(TeleportCakesMod.CONFIG));

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.teleportcakes.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startBooleanToggle(Component.translatable("config.teleportcakes.enable_eating_cakes_sound"),
                        TeleportCakesMod.CONFIG.isEnable_the_sound_of_eating_cakes())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.teleportcakes.enable_eating_cakes_sound.tooltip"))
                .setSaveConsumer(newValue -> TeleportCakesMod.CONFIG.setEnable_the_sound_of_eating_cakes(newValue))
                .build()
        );

        general.addEntry(entryBuilder
                .startBooleanToggle(Component.translatable("config.teleportcakes.enable_tooltips_for_displaying_item"),
                        TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.teleportcakes.enable_tooltips_for_displaying_item.tooltip"))
                .setSaveConsumer(newValue -> TeleportCakesMod.CONFIG.setEnable_tooltips_for_displaying_item(newValue))
                .build()
        );

        return builder.build();
    }
    
}
