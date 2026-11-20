package de.john.droptweaks.mixin;

import de.john.droptweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * LocalPlayer#drop wird ausschliesslich von der Drop-Taste in der Hotbar benutzt.
 * Drops in Inventar/Kisten/Oefen laufen ueber handleInventoryMouseClick (THROW) und sind NICHT betroffen.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void johnsdroptweaks$blockHotbarDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        if (Config.isBlocked(self.getMainHandItem())) {
            cir.setReturnValue(false);
        }
    }
}
