package top.ilov.mcmods.tc.platform.services;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IRegisterHelper {
    <T extends Block> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier);
}
