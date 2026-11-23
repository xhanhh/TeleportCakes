package top.ilov.mcmods.tc.items.cupcakes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.items.CupcakeBaseItem;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public class OverworldCupcakeItem extends CupcakeBaseItem {

    public OverworldCupcakeItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isTeleportBlocked(@NotNull Level level) {
        return false;
    }

    @Override
    @NotNull
    protected String getTeleportBlockedMessageKey() {
        return "msg.teleportcakes.cannot_eat_overworld_cupcake";
    }

    @Override
    @NotNull
    protected String getTooltipTranslationKey() {
        return "tooltip.teleportcakes.overworld_cupcake";
    }

    @Override
    protected boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
        return CakeTeleporter.teleportToOverworld(player);
    }

}
