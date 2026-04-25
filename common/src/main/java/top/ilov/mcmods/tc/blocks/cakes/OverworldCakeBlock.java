package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import top.ilov.mcmods.tc.blocks.CakeBaseBlock;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public class OverworldCakeBlock extends CakeBaseBlock {

    public OverworldCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NonNull
    public InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                           Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.MILK_BUCKET && state.getValue(BITES) > 0 && !level.isClientSide()) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    @Override
    protected boolean isTeleportBlocked(@NonNull ServerLevel level) {
        return level.dimension() == Level.OVERWORLD;
    }

    @Override
    @NonNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_overworld_cake";
    }

    @Override
    protected boolean teleport(@NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull ServerPlayer player) {
        return CakeTeleporter.teleportToOverworld(player);
    }

}
