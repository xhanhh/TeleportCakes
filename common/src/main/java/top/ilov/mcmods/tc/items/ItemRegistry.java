package top.ilov.mcmods.tc.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.EndCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.NetherCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.OverworldCupcakeItem;
import top.ilov.mcmods.tc.platform.Services;

import java.util.function.Supplier;

public class ItemRegistry {

    private static final FoodProperties CUPCAKE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.1F)
            .alwaysEdible()
            .build();

    private static final FoodProperties AGGREGATE_CUPCAKE_FOOD = new FoodProperties.Builder()
            .nutrition(0)
            .saturationModifier(0.0F)
            .alwaysEdible()
            .build();

    public static final Supplier<Item> paper_liner = registerItem("paper_liner",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final Supplier<Item> aggregate_cupcake = registerItem("aggregate_cupcake",
            () -> new AggregateCupcakeItem(new Item.Properties()
                    .stacksTo(1)
                    .food(AGGREGATE_CUPCAKE_FOOD)));

    public static final Supplier<Item> overworld_cupcake = registerItem("overworld_cupcake",
            () -> new OverworldCupcakeItem(defaultCupcakeProperties()));

    public static final Supplier<Item> nether_cupcake = registerItem("nether_cupcake",
            () -> new NetherCupcakeItem(defaultCupcakeProperties()));

    public static final Supplier<Item> end_cupcake = registerItem("end_cupcake",
            () -> new EndCupcakeItem(defaultCupcakeProperties()));

    private static Item.Properties defaultCupcakeProperties() {
        return new Item.Properties()
                .stacksTo(64)
                .food(CUPCAKE_FOOD);
    }

    private static Supplier<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return Services.REGISTER.registerItem(name, itemSupplier);
    }

    public static void registerItems() {}

}
