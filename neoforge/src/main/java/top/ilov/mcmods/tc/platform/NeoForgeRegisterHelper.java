package top.ilov.mcmods.tc.platform;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.function.Supplier;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TeleportCakesMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TeleportCakesMod.MOD_ID);


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
