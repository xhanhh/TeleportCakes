package top.ilov.mcmods.tc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRespawnLogic.class)
public interface PlayerRespawnLogicInvoker {

    @Invoker("getOverworldRespawnPos")
    @Nullable
    static BlockPos teleportcakes$getOverworldRespawnPos(ServerLevel level, int x, int z) {
        throw new AssertionError();
    }

}
