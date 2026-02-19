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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;

import java.util.Optional;

public class OverworldCakeBlock extends CakeTeleportBase {

    public OverworldCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (player.isSpectator()) return InteractionResult.PASS;

        if (itemStack.isEmpty()) {

            if (level.dimension() == Level.OVERWORLD) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_overworld_cake"), true);
                }
                return InteractionResult.PASS;
            }

            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!(level instanceof ServerLevel currentLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.FAIL;
            }

            ResourceKey<Level> registryKey = Level.OVERWORLD;
            ServerLevel serverLevel = currentLevel.getServer().getLevel(registryKey);

            if (serverLevel == null) {
                return InteractionResult.FAIL;
            }

            float yaw = player.getYRot();
            float pitch = player.getXRot();

            BlockPos respawnPos = serverPlayer.getRespawnPosition();
            float respawnAngle = serverPlayer.getRespawnAngle();

            Optional<Vec3> safePosOpt = Optional.empty();
            if (respawnPos != null) {
                safePosOpt = Player.findRespawnPositionAndUseSpawnBlock(
                        serverLevel, respawnPos, respawnAngle, false, true);
            }

            Vec3 targetPos;
            if (safePosOpt.isPresent()) {
                targetPos = safePosOpt.get();
            } else {
                BlockPos worldSpawn = serverLevel.getSharedSpawnPos();
                targetPos = Vec3.atCenterOf(worldSpawn);
            }

            serverPlayer.teleportTo(serverLevel, targetPos.x, targetPos.y, targetPos.z, yaw, pitch);

            return super.use(state, level, pos, player, hand, hit);

        }

        if (itemStack.is(Items.MILK_BUCKET) && state.getValue(BITES) > 0) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }


}
