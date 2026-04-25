package top.ilov.mcmods.tc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;

import java.util.Map;

public class TCFabricClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
            if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;

            Map<Item, String> tooltipMap = Map.of(
                    BlocksRegistry.end_cake.get().asItem(), "tooltip.teleportcakes.end_cake",
                    BlocksRegistry.nether_cake.get().asItem(), "tooltip.teleportcakes.nether_cake"
            );

            String translationKey = tooltipMap.get(stack.getItem());
            if (translationKey == null) return;

            if (TeleportCakesMod.CONFIG.isEnable_tooltips_for_shift_displaying_item()) {

                if (Minecraft.getInstance().hasShiftDown()) {
                    tooltip.add(Component.translatable(translationKey));
                } else {
                    tooltip.add(Component.translatable("tooltip.teleportcakes.shift"));
                }

            }

        });
    }
}
