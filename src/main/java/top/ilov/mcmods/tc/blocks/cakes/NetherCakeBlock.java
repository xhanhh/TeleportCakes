package top.ilov.mcmods.tc.blocks.cakes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ilov.mcmods.tc.TeleportCakesMod;
import top.ilov.mcmods.tc.blocks.CakeTeleportBase;
import top.ilov.mcmods.tc.utils.NetherTeleportHelper;

import java.util.List;

public class NetherCakeBlock extends CakeTeleportBase {

    public NetherCakeBlock(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty BITES = BlockStateProperties.BITES;

    @Override
    @NotNull
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel
                && !player.isSpectator() && itemStack.isEmpty() && !player.isPassenger()) {

            if (serverLevel.dimension().equals(Level.NETHER)) {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_nether_cake"), true);
                return InteractionResult.PASS;
            }

            ServerLevel nether = serverLevel.getServer().getLevel(Level.NETHER);
            if (nether == null) {
                return InteractionResult.FAIL;
            }

            if (player instanceof ServerPlayer serverPlayer
                    && !NetherTeleportHelper.hasWorldNetherSpawn(nether)
                    && !NetherTeleportHelper.hasSavedNetherSpawn(serverPlayer)) {
                serverPlayer.displayClientMessage(Component.translatable("msg.teleportcakes.creating_nether_spwan_point"), true);
            }

            @NotNull BlockPos spawnPos;
            if (player instanceof ServerPlayer serverPlayer) {
                spawnPos = NetherTeleportHelper.getOrCreateNetherSpawn(nether, serverPlayer, player.getDirection());
            } else {
                BlockPos found = NetherTeleportHelper.findSafeNetherSpawn(nether, player);
                spawnPos = found != null ? found : new BlockPos(0, 70, 0);
                NetherTeleportHelper.checkPlatformAndPlaceCake(nether, spawnPos, player.getDirection());
            }

            Vec3 targetPos = spawnPos.getCenter();
            float yaw = player.getYRot();
            float pitch = player.getXRot();

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(nether, targetPos.x, targetPos.y, targetPos.z, yaw, pitch);
            } else {
                return InteractionResult.FAIL;
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        if (itemStack.is(Items.OBSIDIAN) && state.getValue(BITES) > 0) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.teleportcakes.nether_cake"));
        } else {
            tooltip.add(Component.translatable("tooltip.teleportcakes.shift"));
        }

    }

}
