package top.ilov.mcmods.tc.blocks.cakes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

import java.util.List;

public class EndCakeBlock extends CakeTeleportBase {

    public EndCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide && !player.isSpectator() && itemStack.isEmpty()) {

            if (level.dimension() != Level.END) {

                ResourceKey<Level> registryKey = level.dimension() == Level.END ? Level.OVERWORLD : Level.END;
                ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(registryKey);

                BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;

                if (targetLevel == null) {
                    return InteractionResult.FAIL;
                }

                Entity teleportedEntity = player.changeDimension(targetLevel);
                if (teleportedEntity instanceof Player teleportedPlayer) {
                    teleportedPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                }

                BlockPos cakePos = spawnPos.relative(player.getDirection());
                while (targetLevel.isEmptyBlock(cakePos.below())) {
                    cakePos = cakePos.below();
                }

                if (targetLevel.isEmptyBlock(cakePos)) {
                    targetLevel.setBlock(cakePos, BlocksRegistry.overworld_cake.defaultBlockState(), 3);
                }

                return eat(level, pos, state, player);
            } else {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_end_cake"), true);
            }
        }

        if (itemStack.getItem() == Items.ENDER_EYE && state.getValue(BITES) > 0 && !level.isClientSide) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
        }

        if (level.isClientSide && itemStack.isEmpty()) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.teleportcakes.end_cake"));
        } else {
            tooltip.add(Component.translatable("tooltip.teleportcakes.shift"));
        }

    }

}
