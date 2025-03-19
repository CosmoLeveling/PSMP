package com.cosmo.psmp.entities.behaviours;

import com.cosmo.psmp.entities.custom.PumpkinGuyEntity;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Predicate;

public class SetAttackTargetToAttacker<E extends TameableEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryModuleState>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleState.VALUE_PRESENT));

    protected Predicate<? extends LivingEntity> targetPredicate = entity -> true;
    protected Predicate<E> canTargetPredicate = entity -> true;

    public SetAttackTargetToAttacker<E> targetPredicate(Predicate<? extends LivingEntity> predicate) {
        this.targetPredicate = predicate;

        return this;
    }

    public SetAttackTargetToAttacker<E> canTargetPredicate(Predicate<E> predicate) {
        this.canTargetPredicate = predicate;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override // Actually handle the function of the behaviour here
    protected void start(E entity) {
        LivingEntity target=entity.getAttacker();
        if (target == null|| (target instanceof TameableEntity && ((TameableEntity) target).isOwner(entity.getOwner()))||entity.isOwner(target)) { // No valid target, we'll make sure the entity isn't still targeting anything
            BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET);
        } else { // Target found, set the target in memory, and reset the unreachable target timer
            BrainUtils.setMemory(entity, MemoryModuleType.ATTACK_TARGET, target);
            BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
    }
}
