package top.ilov.mcmods.tc.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.blocks.cakes.EndCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.NetherCakeBlock;
import top.ilov.mcmods.tc.blocks.cakes.OverworldCakeBlock;

public class BlocksRegistry {

    public static final CakeBlock end_cake = registerCakeBlock("end_cake",
            new EndCakeBlock(FabricBlockSettings.create().hardness(0.5F).sounds(SoundType.WOOL)));
    public static final CakeBlock overworld_cake = registerCakeBlock("overworld_cake",
            new OverworldCakeBlock(FabricBlockSettings.create().hardness(0.5F).sounds(SoundType.WOOL)));
    public static final CakeBlock nether_cake = registerCakeBlock("nether_cake",
            new NetherCakeBlock(FabricBlockSettings.create().hardness(0.5F).sounds(SoundType.WOOL)));

    protected static CakeBlock registerCakeBlock(String name, CakeBlock block) {

        Registry.register(BuiltInRegistries.BLOCK, TeleportCakesMod.rl(name), block);

        BlockItem blockItem = new BlockItem(block, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, TeleportCakesMod.rl(name), blockItem);

        return block;
    }

    public static void registerBlocks() {}

}
