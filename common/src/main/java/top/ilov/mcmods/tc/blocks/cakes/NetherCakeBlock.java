package top.ilov.mcmods.tc.blocks.cakes;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                    Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() == net.minecraft.world.item.Items.OBSIDIAN
                && state.getValue(BITES) > 0 && !level.isClientSide) {

            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) - 1), 3);
            itemStack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                            @NotNull Player player, @NotNull BlockHitResult hitResult) {

        if (!level.isClientSide && level instanceof ServerLevel serverLevel
                && !player.isSpectator() && !player.isPassenger()) {

            if (!serverLevel.dimension().equals(Level.NETHER)) {
                ServerLevel nether = serverLevel.getServer().getLevel(Level.NETHER);
                if (nether == null) {
                    return InteractionResult.FAIL;
                }

                BlockPos spawnPos = NetherTeleportHelper.findSafeNetherSpawn(nether, player);
                if (spawnPos == null) {
                    spawnPos = new BlockPos(0, 70, 0);
                    NetherTeleportHelper.checkPlatformAndPlaceCake(nether, spawnPos, player.getDirection());
                }

                Vec3 targetPos = spawnPos.getCenter();
                float yaw = player.getYRot();
                float pitch = player.getXRot();

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(nether, targetPos.x, targetPos.y, targetPos.z, yaw, pitch);
                }

                NetherTeleportHelper.checkPlatformAndPlaceCake(nether, spawnPos, player.getDirection());

                return eat(level, pos, state, player);
            } else {
                player.displayClientMessage(Component.translatable("msg.teleportcakes.cannot_eat_nether_cake"), true);
                return InteractionResult.PASS;
            }
        }

        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (!TeleportCakesMod.CONFIG.isEnable_tooltips_for_displaying_item()) return;

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.teleportcakes.nether_cake"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.teleportcakes.shift"));
        }

    }

}
