package top.ilov.mcmods.tc.platform;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.function.Supplier;

public class ForgeRegisterHelper implements IRegisterHelper {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TeleportCakesMod.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TeleportCakesMod.MOD_ID);


    @Override
    public <T extends Block> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier) {

        Supplier<T> registeredBlock = BLOCKS.register(name, blockSupplier);
        ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;

    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

}
