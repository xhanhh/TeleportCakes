package top.ilov.mcmods.tc.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.blocks.cakes.EndCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.NetherCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.OverworldCakeBlock;
import top.ilov.mcmods.tc.platform.Services;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlocksRegistry {

    public static final Supplier<CakeBlock> end_cake = registerCakeBlock("end_cake",
            id -> new EndCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)
                    .setId(ResourceKey.create(Registries.BLOCK, id)))
    );

    public static final Supplier<CakeBlock> overworld_cake = registerCakeBlock("overworld_cake",
            id -> new OverworldCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)
                    .setId(ResourceKey.create(Registries.BLOCK, id)))
    );

    public static final Supplier<CakeBlock> nether_cake = registerCakeBlock("nether_cake",
            id -> new NetherCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)
                    .setId(ResourceKey.create(Registries.BLOCK, id)))
    );

    private static <T extends CakeBlock> Supplier<T> registerCakeBlock(String name, Function<ResourceLocation, T> blockFactory) {
        return Services.REGISTER.registerCakeBlock(name, () -> blockFactory.apply(TeleportCakesMod.rl(name)));
    }

    public static void registerBlocks() {}

}
