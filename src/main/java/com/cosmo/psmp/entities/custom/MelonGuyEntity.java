package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.behaviours.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearestItemSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MelonGuyEntity extends MinionEntity implements SmartBrainOwner<MelonGuyEntity> {
    public MelonGuyEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void mobTick() {
        if(!isMobSitting()) {
            tickBrain(this);
        }else{
            BrainUtils.clearMemories(brain,MemoryModuleType.ATTACK_TARGET);
        }
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        MelonGuyEntity melonGuyEntity = PSMPEntities.MELON_GUY.create(world);
        if (melonGuyEntity != null && entity instanceof MelonGuyEntity melonGuyEntity1) {
            if (this.isTamed()) {
                melonGuyEntity.setOwnerUuid(this.getOwnerUuid());
                melonGuyEntity.setTamed(true, true);
            }
        }

        return melonGuyEntity;
    }

    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(other instanceof MelonGuyEntity melonGuyEntity)) {
            return false;
        } else {
            if (!melonGuyEntity.isTamed()) {
                return false;
            } else if (melonGuyEntity.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && melonGuyEntity.isInLove();
            }
        }
    }


    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MelonGuyEntity>> getSensors() {
        return List.of(
                new NearbyItemsSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new NearbyPlayersSensor<>(),
                new NearestItemSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MelonGuyEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new BreedWithPartner<>(),
                new LookAtTarget<>(),
                new FollowOwner<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MelonGuyEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new SetAttackTargetToOwnerAttackTarget<>(),
                        new SetAttackTargetToAttacker<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<PumpkinGuyEntity>(
                        new MinionFarmBehaviour<>(),
                        new PickupItemBehaviour<>(),
                        new SetRandomWalkTargetTamed<>(),
                        new Idle<>().runFor(livingEntity -> livingEntity.getRandom().nextBetween(30,60) )
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends MelonGuyEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)
        );
    }
}
