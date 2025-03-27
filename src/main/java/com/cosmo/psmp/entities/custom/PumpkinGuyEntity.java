package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.behaviours.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

public class PumpkinGuyEntity extends MinionEntity implements SmartBrainOwner<PumpkinGuyEntity> {
    public PumpkinGuyEntity(EntityType<? extends TameableEntity> entityType, World world) {
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
        PumpkinGuyEntity pumpkinGuyEntity = PSMPEntities.PUMPKIN_GUY.create(world);
        if (pumpkinGuyEntity != null && entity instanceof PumpkinGuyEntity pumpkinGuyEntity1) {
            if (this.isTamed()) {
                pumpkinGuyEntity.setOwnerUuid(this.getOwnerUuid());
                pumpkinGuyEntity.setTamed(true, true);
            }
        }

        return pumpkinGuyEntity;
    }

    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(other instanceof PumpkinGuyEntity pumpkinGuyEntity)) {
            return false;
        } else {
            if (!pumpkinGuyEntity.isTamed()) {
                return false;
            } else if (pumpkinGuyEntity.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && pumpkinGuyEntity.isInLove();
            }
        }
    }


    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends PumpkinGuyEntity>> getSensors() {
        return List.of(
                new NearbyItemsSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new NearbyPlayersSensor<>(),
                new NearestItemSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends PumpkinGuyEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new BreedWithPartner<>(),
                new LookAtTarget<>(),
                new FollowOwner<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends PumpkinGuyEntity> getIdleTasks() {
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
    public BrainActivityGroup<? extends PumpkinGuyEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)
        );
    }
}
