package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.ilov.mcmods.tc.items.CupcakeBaseItem;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public class NetherCupcakeItem extends CupcakeBaseItem {

    public NetherCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isTeleportBlocked(@NonNull Level level) {
        return level.dimension().equals(Level.NETHER);
    }

    @Override
    @NonNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_nether_cupcake";
    }

    @Override
    @NonNull
    protected String getTooltipTranslationKey() {
        return "tooltip.teleportcakes.nether_cupcake";
    }

    @Override
    protected boolean teleport(@NonNull ServerLevel level, @NonNull ServerPlayer player) {
        return CakeTeleporter.teleportToNether(level, player);
    }
}
