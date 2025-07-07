package top.ilov.mcmods.tc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

import java.util.Map;

public class TCFabricMod implements ModInitializer {
    
    @Override
    public void onInitialize() {

        TeleportCakesMod.init();
        registerItemGroup();

    }

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            TeleportCakesMod.rl("group"));

    public static void registerItemGroup() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricItemGroup.builder()
                .title(Component.translatable("teleportcakes.name"))
                .icon(() -> new ItemStack(BlocksRegistry.end_cake.get()))
                .displayItems((context, entries) -> {
                    entries.accept(BlocksRegistry.overworld_cake.get());
                    entries.accept(BlocksRegistry.nether_cake.get());
                    entries.accept(BlocksRegistry.end_cake.get());
                })
                .build()
        );

        ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {

            if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) {
                return;
            }

            Map<Item, String> tooltipMap = Map.of(
                    BlocksRegistry.end_cake.get().asItem(), "tooltip.teleportcakes.end_cake",
                    BlocksRegistry.nether_cake.get().asItem(), "tooltip.teleportcakes.nether_cake"
            );

            String translationKey = tooltipMap.get(stack.getItem());

            if (translationKey != null) {
                if (Screen.hasShiftDown()) {
                    tooltip.add(Component.translatable(translationKey));
                } else {
                    tooltip.add(Component.translatable("tooltip.teleportcakes.shift"));
                }
            }
        });
    }

}
