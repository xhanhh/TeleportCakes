package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

public class OverworldCakeBlock extends CakeTeleportBase {

    public OverworldCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide && !player.isSpectator() && itemStack.isEmpty()) {
            if (level.dimension() != Level.OVERWORLD) {

                ResourceKey<Level> registryKey = Level.OVERWORLD;
                ServerLevel serverLevel = ((ServerLevel) level).getServer().getLevel(registryKey);

                if (serverLevel == null) {
                    return InteractionResult.FAIL;
                }

                BlockPos spawnPos = serverLevel.getSharedSpawnPos();
                double x = spawnPos.getX() + 0.5;
                double y = spawnPos.getY();
                double z = spawnPos.getZ() + 0.5;
                float yaw = player.getYRot();
                float pitch = player.getXRot();

                eat(level, pos, state, player);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(serverLevel, x, y, z, yaw, pitch);
                    serverPlayer.setPos(spawnPos.getX() + 1, spawnPos.getY(), spawnPos.getZ());
                }

                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_overworld_cake"), true);
            }
        }

        if (itemStack.getItem() == Items.MILK_BUCKET && state.getValue(BITES) > 0 && !level.isClientSide) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        }

        if (level.isClientSide && itemStack.isEmpty()) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (itemStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }


}
