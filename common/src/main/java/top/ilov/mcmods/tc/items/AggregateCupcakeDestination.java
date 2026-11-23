package top.ilov.mcmods.tc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import top.ilov.mcmods.tc.utils.CakeTeleporter;

public enum AggregateCupcakeDestination {

    OVERWORLD("destination.teleportcakes.overworld") {
        @Override
        public boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
            return CakeTeleporter.teleportToOverworld(player);
        }
    },

    NETHER("destination.teleportcakes.nether") {
        @Override
        public boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
            return CakeTeleporter.teleportToNether(level, player);
        }
    },

    END("destination.teleportcakes.end") {
        @Override
        public boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player) {
            return CakeTeleporter.teleportToEnd(level, player);
        }
    };

    private final String translationKey;

    AggregateCupcakeDestination(String translationKey) {
        this.translationKey = translationKey;
    }

    @NotNull
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @NotNull
    public static AggregateCupcakeDestination fromIndex(int index) {
        AggregateCupcakeDestination[] values = values();
        return values[Math.floorMod(index, values.length)];
    }

    public abstract boolean teleport(@NotNull ServerLevel level, @NotNull ServerPlayer player);

}
