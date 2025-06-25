package top.ilov.mcmods.tc.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.ilov.mcmods.tc.blocks.cakes.EndCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.NetherCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.OverworldCakeBlock;
import top.ilov.mcmods.tc.platform.Services;

import java.util.function.Supplier;

public class BlocksRegistry {

    public static final Supplier<CakeBlock> end_cake = registerCakeBlock("end_cake",
            () -> new EndCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    public static final Supplier<CakeBlock> overworld_cake = registerCakeBlock("overworld_cake",
            () -> new OverworldCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    public static final Supplier<CakeBlock> nether_cake = registerCakeBlock("nether_cake",
            () -> new NetherCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    private static <T extends CakeBlock> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier) {
        return Services.REGISTER.registerCakeBlock(name, blockSupplier);
    }

    public static void registerBlocks() {}

}
