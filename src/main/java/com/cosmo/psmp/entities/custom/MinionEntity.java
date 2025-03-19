package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.entities.PSMPEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MinionEntity extends TameableEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> SWORD = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BACKPACK = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public MinionEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,20);
    }


    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 20;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            this.setupAnimationStates();
        }
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_WOOD_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.BONE_MEAL);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (!this.getWorld().isClient || this.isBaby() && this.isBreedingItem(itemStack)) {
            if (this.isTamed()) {
                if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    itemStack.decrementUnlessCreative(1, player);
                    FoodComponent foodComponent = (FoodComponent) itemStack.get(DataComponentTypes.FOOD);
                    float f = foodComponent != null ? (float) foodComponent.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    return ActionResult.success(this.getWorld().isClient());
                } else if (!this.hasSword() && itemStack.isOf(Items.IRON_SWORD)) {
                    this.setSWORD(true);
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.success(this.getWorld().isClient());
                } else {
                    ActionResult actionResult = super.interactMob(player, hand);
                    if (!actionResult.isAccepted() && this.isOwner(player)) {
                        this.setSitting(!this.isMobSitting());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((LivingEntity) null);
                        return ActionResult.SUCCESS_NO_ITEM_USED;
                    } else {
                        return actionResult;
                    }
                }
            } else if (itemStack.isOf(Items.BONE)) {
                itemStack.decrementUnlessCreative(1, player);
                this.tryTame(player);
                return ActionResult.SUCCESS;
            } else {
                return super.interactMob(player, hand);
            }
        } else {
            boolean bl = this.isOwner(player) || this.isTamed() || itemStack.isOf(Items.BONE) && !this.isTamed();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        }
    }

    private void tryTame(PlayerEntity player) {
        if (this.random.nextInt(3) == 0) {
            this.setOwner(player);
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
            this.setSitting(true);
            this.getWorld().sendEntityStatus(this, (byte)7);
        } else {
            this.getWorld().sendEntityStatus(this, (byte)6);
        }

    }
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(other instanceof MinionEntity)) {
            return false;
        } else {
            MinionEntity minionEntity = (MinionEntity) other;
            if (!minionEntity.isTamed()) {
                return false;
            } else if (minionEntity.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && minionEntity.isInLove();
            }
        }
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SWORD,false);
        builder.add(SITTING,false);
        builder.add(BACKPACK,false);
    }

    public void setSWORD(boolean sword) {
        if (sword) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)8.0F);
        }else{
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)1.0F);
        }
        this.dataTracker.set(SWORD,sword);
    }
    public boolean hasSword() {
        return (Boolean)this.dataTracker.get(SWORD);
    }
    public void setBackpack(boolean backpack) {
        this.dataTracker.set(BACKPACK,backpack);
    }
    public boolean hasBackpack() {
        return (Boolean)this.dataTracker.get(BACKPACK);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(SITTING,sitting);
    }

    public boolean isMobSitting() {
        return this.dataTracker.get(SITTING);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sword",this.hasSword());
        nbt.putBoolean("Backpack",this.hasBackpack());
        nbt.putBoolean("isSitting",this.isMobSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SWORD,nbt.getBoolean("Sword"));
        this.dataTracker.set(BACKPACK,nbt.getBoolean("Backpack"));
        this.dataTracker.set(SITTING,nbt.getBoolean("isSitting"));
    }

    @Override
    protected void dropInventory() {
        if (this.hasSword()){
            ItemStack itemStack = new ItemStack(Items.IRON_SWORD);
            this.dropStack(itemStack);
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        setSWORD(false);
        setBackpack(false);
        setSitting(false);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
