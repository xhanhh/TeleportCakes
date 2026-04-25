package top.ilov.mcmods.tc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.items.ItemRegistry;

import java.util.function.Supplier;

public class NeoForgeItemGroup {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, TeleportCakesMod.MOD_ID);

    public static final Supplier<CreativeModeTab> CAKE_TAB = CREATIVE_MODE_TABS.register("group", () -> CreativeModeTab.builder()
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

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
