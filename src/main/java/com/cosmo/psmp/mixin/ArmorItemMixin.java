package com.cosmo.psmp.mixin;

import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(method = "dispenseArmor",at = @At("Head"),cancellable = true)
    private static void dispense(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info){
        BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
        List<LivingEntity> list = pointer.world().getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.Equipable(armor)));
        if(list.getFirst() instanceof MinionEntity) {
            info.setReturnValue(false);
        }
    }
}
