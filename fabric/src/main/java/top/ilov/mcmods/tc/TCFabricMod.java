package top.ilov.mcmods.tc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.items.ItemRegistry;
import top.ilov.mcmods.tc.network.FabricPayloads;

public class TCFabricMod implements ModInitializer {
    
    @Override
    public void onInitialize() {

        FabricPayloads.register();
        TeleportCakesMod.init();
        registerItemGroup();

    }

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            TeleportCakesMod.rl("group"));

    public static void registerItemGroup() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricCreativeModeTab.builder()
                .title(Component.translatable("teleportcakes.name"))
                .icon(() -> new ItemStack(BlocksRegistry.end_cake.get()))
                .displayItems((context, entries) -> {
                    entries.accept(BlocksRegistry.overworld_cake.get());
                    entries.accept(BlocksRegistry.nether_cake.get());
                    entries.accept(BlocksRegistry.end_cake.get());
                    entries.accept(ItemRegistry.paper_liner.get());
                    entries.accept(ItemRegistry.aggregate_cupcake.get());
                    entries.accept(ItemRegistry.overworld_cupcake.get());
                    entries.accept(ItemRegistry.nether_cupcake.get());
                    entries.accept(ItemRegistry.end_cupcake.get());
                })
                .build()
        );
    }

}
