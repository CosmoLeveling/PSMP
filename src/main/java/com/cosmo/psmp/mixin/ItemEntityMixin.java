package com.cosmo.psmp.mixin;

import com.cosmo.psmp.items.PSMPItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract net.minecraft.item.ItemStack getStack();

    @Inject(method = "damage",at = @At("HEAD"), cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (!this.getStack().isEmpty() && this.getStack().isOf(PSMPItems.Corrupt_Star) && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
        }
    }

}
