package com.cosmo.psmp.entities.behaviours;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;

public class SetRandomWalkTargetTamed<T extends TameableEntity> extends SetRandomWalkTarget<T> {
    @Override
    protected boolean doStartCheck(ServerWorld level, T entity, long gameTime) {
        return !entity.isSitting();
    }
}
