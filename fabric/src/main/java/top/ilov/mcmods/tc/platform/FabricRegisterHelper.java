package top.ilov.mcmods.tc.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.function.Supplier;

public class FabricRegisterHelper implements IRegisterHelper {

    @Override
    public <T extends Block> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier) {
        T block = Registry.register(BuiltInRegistries.BLOCK, TeleportCakesMod.rl(name), blockSupplier.get());

        Registry.register(BuiltInRegistries.ITEM, TeleportCakesMod.rl(name), new BlockItem(block, new Item.Properties()));

        return () -> block;
    }

}
