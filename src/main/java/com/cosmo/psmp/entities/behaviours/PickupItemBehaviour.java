package com.cosmo.psmp.entities.behaviours;

import com.cosmo.psmp.entities.custom.MinionEntity;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class PickupItemBehaviour<T extends MinionEntity> extends ExtendedBehaviour<T> {
    private static final List<Pair<MemoryModuleType<?>, MemoryModuleState>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.VALUE_PRESENT),Pair.of(MemoryModuleType.WALK_TARGET,MemoryModuleState.VALUE_PRESENT));
    protected BiPredicate<T, Vec3d> positionPredicate = (entity, pos) -> true;
    protected BiFunction<T, Vec3d, Float> speedModifier = (entity, targetPos) -> 1.0F;
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean doStartCheck(ServerWorld level, T entity, long gameTime) {
        return entity.hasBackpack();
    }

    @Override
    protected void start(T entity) {
        ItemEntity item = BrainUtils.getMemory(entity,MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (item != null){
        Vec3d targetPos = item.getPos();
        if (!this.positionPredicate.test(entity, targetPos)) {
            targetPos = null;
        }

        if (targetPos == null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        } else {
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, (Float)this.speedModifier.apply(entity, targetPos), 0));
        }
        }else{
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        }
    }
}
