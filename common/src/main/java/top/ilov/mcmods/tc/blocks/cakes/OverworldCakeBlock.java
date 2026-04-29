package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.CakeBaseBlock;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public class OverworldCakeBlock extends CakeBaseBlock {

    public OverworldCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                           Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.MILK_BUCKET && state.getValue(BITES) > 0 && !level.isClientSide) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    }

    @Override
    @NotNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_overworld_cake";
    }

    @Override
    protected boolean isTeleportBlocked(@NotNull ServerLevel level) {
        return level.dimension() == Level.OVERWORLD;
    }

    @Override
    protected boolean teleport(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ServerPlayer player) {
        return CakeTeleporter.teleportToOverworld(player);
    }

}
