package top.ilov.mcmods.tc.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.sounds.SoundEvents;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.items.cupcakes.AggregateCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.EndCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.NetherCupcakeItem;
import top.ilov.mcmods.tc.items.cupcakes.OverworldCupcakeItem;
import top.ilov.mcmods.tc.platform.Services;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemRegistry {

    private static final FoodProperties CUPCAKE_FOOD = new FoodProperties(2, 0.1F, true);
    private static final FoodProperties AGGREGATE_CUPCAKE_FOOD = new FoodProperties(0, 0.0F, true);

    private static final Consumable AGGREGATE_CUPCAKE_CONSUMABLE = Consumable.builder()
            .consumeSeconds(1.0F)
            .animation(ItemUseAnimation.EAT)
            .sound(SoundEvents.GENERIC_EAT)
            .build();

    public static final Supplier<Item> paper_liner = registerItem("paper_liner",
            id -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .useItemDescriptionPrefix()
                    .stacksTo(64))
    );

    public static final Supplier<Item> aggregate_cupcake = registerItem("aggregate_cupcake",
            id -> new AggregateCupcakeItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, id))
                    .useItemDescriptionPrefix()
                    .stacksTo(1)
                    .food(AGGREGATE_CUPCAKE_FOOD, AGGREGATE_CUPCAKE_CONSUMABLE))
    );

    public static final Supplier<Item> overworld_cupcake = registerCupcake("overworld_cupcake",
            id -> new OverworldCupcakeItem(defaultCupcakeProperties(id))
    );

    public static final Supplier<Item> nether_cupcake = registerCupcake("nether_cupcake",
            id -> new NetherCupcakeItem(defaultCupcakeProperties(id))
    );

    public static final Supplier<Item> end_cupcake = registerCupcake("end_cupcake",
            id -> new EndCupcakeItem(defaultCupcakeProperties(id))
    );

    private static Item.Properties defaultCupcakeProperties(net.minecraft.resources.Identifier id) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useItemDescriptionPrefix()
                .stacksTo(64)
                .food(CUPCAKE_FOOD);
    }

    private static Supplier<Item> registerCupcake(String name, Function<net.minecraft.resources.Identifier, Item> itemFactory) {
        return Services.REGISTER.registerItem(name, () -> itemFactory.apply(TeleportCakesMod.rl(name)));
    }

    private static Supplier<Item> registerItem(String name, Function<net.minecraft.resources.Identifier, Item> itemFactory) {
        return Services.REGISTER.registerItem(name, () -> itemFactory.apply(TeleportCakesMod.rl(name)));
    }

    public static void registerItems() {}

}
