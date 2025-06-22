package top.ilov.mcmods.tc;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

public class ItemGroup {

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            TeleportCakesMod.rl("group"));

    public static void registerItemGroup() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricItemGroup.builder()
                .title(Component.translatable("teleportcakes.name"))
                .icon(() -> new ItemStack(BlocksRegistry.end_cake))
                .displayItems((context, entries) -> {
                    entries.accept(BlocksRegistry.overworld_cake);
                    entries.accept(BlocksRegistry.nether_cake);
                    entries.accept(BlocksRegistry.end_cake);
                })
                .build()
        );
    }

}
