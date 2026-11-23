package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.items.CupcakeBaseItem;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public class EndCupcakeItem extends CupcakeBaseItem {

    public EndCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isTeleportBlocked(@NotNull Level level) {
        return level.dimension().equals(Level.END);
    }

    @Override
    @NotNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_end_cupcake";
    }

    @Override
    @NotNull
    protected String getTooltipTranslationKey() {
        return "tooltip.teleportcakes.end_cupcake";
    }

    @Override
    protected boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
        return CakeTeleporter.teleportToEnd(level, player);
    }

}
