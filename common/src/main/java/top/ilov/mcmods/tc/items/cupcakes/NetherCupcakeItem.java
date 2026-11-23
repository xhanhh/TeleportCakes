package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.items.CupcakeBaseItem;
import top.ilov.mcmods.tc.utils.CakeTeleporter;
import top.ilov.mcmods.tc.utils.NetherTeleportHelper;

public class NetherCupcakeItem extends CupcakeBaseItem {

    public NetherCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        if (!level.isClientSide
                && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer
                && getBlockedMessageKey(level, player) == null
                && NetherTeleportHelper.shouldShowPreparingSpawnMsg(serverLevel, serverPlayer)) {
            NetherTeleportHelper.showPreparingSpawnMsg(serverPlayer);
        }

        return super.use(level, player, hand);
    }

    @Override
    protected boolean isTeleportBlocked(@NotNull Level level) {
        return level.dimension().equals(Level.NETHER);
    }

    @Override
    @NotNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_nether_cupcake";
    }

    @Override
    @NotNull
    protected String getTooltipTranslationKey() {
        return "tooltip.teleportcakes.nether_cupcake";
    }

    @Override
    protected boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
        return CakeTeleporter.teleportToNether(level, player);
    }

}
