package top.ilov.mcmods.tc.mixin.client;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ilov.mcmods.tc.events.AggregateCupcakeEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void teleportcakes$handleAggregateCupcakeScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        if (AggregateCupcakeEvent.handleScroll(handle, xoffset, yoffset)) {
            ci.cancel();
        }
    }
}
