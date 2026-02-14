package top.ilov.mcmods.tc.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.platform.services.IRegisterHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TeleportCakesMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TeleportCakesMod.MOD_ID);


    @Override
    public <T extends Block> Supplier<T> registerCakeBlock(String name, Supplier<T> blockSupplier) {

        Supplier<T> registeredBlock = BLOCKS.register(name, id -> blockSupplier.get());

        ITEMS.register(name, id -> new BlockItem(
                registeredBlock.get(),
                new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, id))
                        .useBlockDescriptionPrefix()) {
            @Override
            public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext,
                                        @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> components,
                                        @NotNull TooltipFlag tooltipFlag) {

                if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;
                if (name.equals("overworld_cake")) return;

                if (Minecraft.getInstance().hasShiftDown() && Dist.CLIENT.isClient()) {
                    components.accept(Component.translatable("tooltip.teleportcakes." + name));
                } else {
                    components.accept(Component.translatable("tooltip.teleportcakes.shift"));
                }

                super.appendHoverText(pStack, pContext, tooltipDisplay, components, tooltipFlag);
            }

        });

        return registeredBlock;
    }

}
