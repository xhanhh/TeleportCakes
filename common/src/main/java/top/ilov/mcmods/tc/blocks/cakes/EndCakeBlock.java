package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.BlocksRegistry;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

public class EndCakeBlock extends CakeTeleportBase {

    public EndCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                           Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == Items.ENDER_EYE && state.getValue(BITES) > 0 && !level.isClientSide()) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                            @NotNull Player player, @NotNull BlockHitResult hitResult) {

        if (!level.isClientSide() && !player.isSpectator()) {

            if (level.dimension() != Level.END) {

                ResourceKey<Level> registryKey = level.dimension() == Level.END ? Level.OVERWORLD : Level.END;
                ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(registryKey);

                BlockPos spawnPos = ServerLevel.END_SPAWN_POINT;

                if (targetLevel == null) {
                    return InteractionResult.FAIL;
                }

                Vec3 vec3 = spawnPos.getCenter();
                BlockPos platformPos = BlockPos.containing(vec3).below();

                EndPlatformFeature.createEndPlatform(targetLevel, platformPos, true);

                float yaw = Direction.WEST.toYRot();

                eat(level, pos, state, player);

                Player teleportedPlayer = (Player) player.teleport(
                        new TeleportTransition(targetLevel, vec3, player.getKnownMovement(), yaw, player.getXRot(),
                                TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)));

                if (teleportedPlayer != null) {
                    teleportedPlayer.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                }

                BlockPos cakePos = spawnPos.relative(player.getDirection());
                while (targetLevel.isEmptyBlock(cakePos.below())) {
                    cakePos = cakePos.below();
                }

                if (targetLevel.isEmptyBlock(cakePos)) {
                    targetLevel.setBlock(cakePos, BlocksRegistry.overworld_cake.get().defaultBlockState(), 3);
                }

                return eat(level, pos, state, player);
            } else {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_end_cake"), true);
                return InteractionResult.PASS;
            }
        }

        if (level.isClientSide()) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

}
