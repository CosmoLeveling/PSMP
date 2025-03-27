package com.cosmo.psmp.entities.behaviours;

import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;

public class MinionFollowOwner<T extends MinionEntity,E extends LivingEntity> extends FollowOwner<T> {
    @Override
    protected boolean checkExtraStartConditions(ServerWorld level, T entity) {
        if(entity.getState().equals(MinionEntity.State.FOLLOWING)) {
            return super.checkExtraStartConditions(level, entity);
        }else{
            return false;
        }
    }
}
