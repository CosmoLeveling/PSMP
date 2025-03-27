package com.cosmo.psmp.mixin;


import com.cosmo.psmp.effects.PSMPEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

	@Inject(method = "updatePotionVisibility", at = @At("HEAD"),cancellable = true)
	private void hasSplit(CallbackInfo ci){
		if(!this.hasStatusEffect(null)&this.hasStatusEffect(PSMPEffects.TRUE_INVISIBILITY)){
			ci.cancel();
		}
	}
}
