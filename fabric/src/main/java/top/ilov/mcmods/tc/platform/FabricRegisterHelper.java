package top.ilov.mcmods.tc.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.function.Supplier;

public class FabricRegisterHelper implements IRegisterHelper {

    @Override
    public <T extends Block> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier) {

        ResourceLocation id = TeleportCakesMod.rl(name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        T block = blockSupplier.get();

        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Item.Properties props = new Item.Properties().setId(itemKey).useBlockDescriptionPrefix();
        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, props));

        return () -> block;
    }

}
