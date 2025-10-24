package com.cosmo.psmp.mixin;

import com.cosmo.psmp.effects.PSMPEffects;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo callbackInfo) {
        if (MinecraftClient.getInstance().player != null) {
            if (MinecraftClient.getInstance().player.hasStatusEffect(PSMPEffects.IMMOBILIZED) && !MinecraftClient.getInstance().isPaused()) {
                KeyBinding.unpressAll();
                callbackInfo.cancel();
            }
        }
    }
}
