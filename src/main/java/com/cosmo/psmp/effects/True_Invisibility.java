package com.cosmo.psmp.effects;

import com.cosmo.psmp.util.ModCustomEntityAttachedData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.cosmo.psmp.PSMPAttachmentTypes.ENTITIES;

public class True_Invisibility extends StatusEffect {
    protected True_Invisibility(StatusEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (!(entity.getStatusEffect(PSMPEffects.TRUE_INVISIBILITY).getDuration() > 1)) {
                syncList(entity, 2);
            }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        syncList(entity,1);
    }

    public static void syncList(LivingEntity entity, int test) {
        if (entity.getServer() != null) {
            for (World world: entity.getServer().getWorlds()) {
                if (test == 1) {
                    ModCustomEntityAttachedData data = world.getAttachedOrElse(ENTITIES, ModCustomEntityAttachedData.DEFAULT);
                    world.setAttached(ENTITIES, data.addUuid(entity.getUuid()));
                } else {
                    ModCustomEntityAttachedData data = world.getAttachedOrElse(ENTITIES, ModCustomEntityAttachedData.DEFAULT);
                    world.setAttached(ENTITIES, data.removeUuid(entity.getUuid()));
                }
            }
        }
    }
}
