package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.PSMP;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MinionEntity extends TameableEntity implements InventoryOwner {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final SimpleInventory inventory = new SimpleInventory(9);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> ARMOR = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SWORD = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BACKPACK = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public MinionEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void loot(ItemEntity item) {
        if(this.hasBackpack()) {
            ItemStack itemStack = item.getStack();
            ItemStack itemStack1 = calculatePickedUpStack(itemStack);
            if (!itemStack1.isEmpty()) {
                this.triggerItemPickedUpByEntityCriteria(item);
                this.sendPickup(item, itemStack1.getCount());
                this.inventory.addStack(itemStack);
                itemStack.decrement(itemStack1.getCount());
                if (itemStack.isEmpty()) {
                    item.discard();
                }
            }
        }
    }
    public boolean areInventoriesDifferent(Inventory inventory) {
        return this.inventory != inventory;
    }
    public ItemStack calculatePickedUpStack(ItemStack itemStack) {
        ItemStack returnedStack =ItemStack.EMPTY;
        if(inventory.isEmpty()){
            returnedStack = itemStack;
        }else{
            for(ItemStack itemStack1:inventory.getHeldStacks()){
                if (itemStack1.isEmpty()){
                    returnedStack = itemStack;
                } else if (itemStack1.isOf(itemStack.getItem())) {
                    if(itemStack1.getCount() != itemStack1.getMaxCount()) {
                        int i = itemStack1.getCount() + itemStack.getCount();
                        if (i >= 64) {
                            int k = itemStack.getCount() - (i - 64);
                            returnedStack = new ItemStack(itemStack.getItem(), k);
                        } else {
                            returnedStack = new ItemStack(itemStack.getItem(), itemStack.getCount());
                        }
                    }
                }
            }
        }
        return returnedStack;
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
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity) && !(target instanceof ArmorStandEntity)) {
            if (target instanceof WolfEntity) {
                WolfEntity wolfEntity = (WolfEntity)target;
                return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
            } else {
                if (target instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity)target;
                    if (owner instanceof PlayerEntity) {
                        PlayerEntity playerEntity2 = (PlayerEntity)owner;
                        if (!playerEntity2.shouldDamagePlayer(playerEntity)) {
                            return false;
                        }
                    }
                }

                if (target instanceof AbstractHorseEntity) {
                    AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)target;
                    if (abstractHorseEntity.isTame()) {
                        return false;
                    }
                }

                boolean var10000;
                if (target instanceof TameableEntity) {
                    TameableEntity tameableEntity = (TameableEntity)target;
                    if (tameableEntity.isTamed()) {
                        var10000 = false;
                        return var10000;
                    }
                }

                var10000 = true;
                return var10000;
            }
        } else {
            return false;
        }
    }
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (!this.getWorld().isClient || this.isBaby() && this.isBreedingItem(itemStack)) {
            if (this.isTamed()) {
                if (player.shouldCancelInteraction()) {
                    this.openInventory(player);
                    return ActionResult.success(this.getWorld().isClient);
                }else if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    itemStack.decrementUnlessCreative(1, player);
                    FoodComponent foodComponent = (FoodComponent) itemStack.get(DataComponentTypes.FOOD);
                    float f = foodComponent != null ? (float) foodComponent.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    return ActionResult.success(this.getWorld().isClient());
                } else if (!this.hasSword() && itemStack.isOf(Items.IRON_SWORD) && this.isOwner(player)) {
                    this.setSWORD(true);
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.success(this.getWorld().isClient());
                } else if (Objects.equals(this.getARMOR(), "None") && (itemStack.isOf(Items.CHAINMAIL_HELMET)||itemStack.isOf(Items.IRON_HELMET)||itemStack.isOf(Items.GOLDEN_HELMET)||itemStack.isOf(Items.LEATHER_HELMET)||itemStack.isOf(Items.DIAMOND_HELMET)||itemStack.isOf(Items.NETHERITE_HELMET)) && this.isOwner(player)) {
                    if(itemStack.isOf(Items.CHAINMAIL_HELMET)){
                        this.setArmor("Chain");
                    } else if (itemStack.isOf(Items.IRON_HELMET)) {
                        this.setArmor("Iron");
                    } else if (itemStack.isOf(Items.LEATHER_HELMET)) {
                        this.setArmor("Leather");
                    } else if (itemStack.isOf(Items.GOLDEN_HELMET)) {
                        this.setArmor("Gold");
                    } else if (itemStack.isOf(Items.DIAMOND_HELMET)) {
                        this.setArmor("Diamond");
                    } else if (itemStack.isOf(Items.NETHERITE_HELMET)) {
                        this.setArmor("Netherite");
                    }
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.success(this.getWorld().isClient());
                }else if (!this.hasBackpack() && itemStack.isOf(Items.CHEST) && this.isOwner(player)) {
                    this.setBackpack(true);
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
    public void openInventory(PlayerEntity player) {
        if (!this.getWorld().isClient && this.isTamed() && this.isOwner(player)) {
            for (int i = 0; i < this.inventory.size(); ++i) {
                ItemStack itemStack = this.inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    this.dropStack(itemStack);
                    inventory.removeStack(i);
                }
            }
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
        builder.add(ARMOR,"None");
        builder.add(SITTING,false);
        builder.add(BACKPACK,false);
    }

    public void setSWORD(boolean sword) {
        if (sword) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)100.0F);
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

    public String getARMOR() {
        return this.dataTracker.get(ARMOR);
    }
    public void setArmor(String string) {
        if (Objects.equals(string, "Leather")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(7);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (Objects.equals(string, "Chain")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(12);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (Objects.equals(string, "Iron")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(15);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (Objects.equals(string, "Gold")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(11);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (Objects.equals(string, "Diamond")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(20);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(8);
        }else if (Objects.equals(string, "Netherite")){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(20);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(12);
        }
        this.dataTracker.set(ARMOR,string);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sword",this.hasSword());
        nbt.putString("Armor",this.getARMOR());
        nbt.putBoolean("Backpack",this.hasBackpack());
        nbt.putBoolean("isSitting",this.isMobSitting());
        this.writeInventory(nbt,this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SWORD,nbt.getBoolean("Sword"));
        this.dataTracker.set(BACKPACK,nbt.getBoolean("Backpack"));
        this.dataTracker.set(SITTING,nbt.getBoolean("isSitting"));
        this.dataTracker.set(ARMOR,nbt.getString("Armor"));
        this.readInventory(nbt,this.getRegistryManager());
    }

    @Override
    protected void dropInventory() {
        if (this.hasSword()){
            ItemStack itemStack = new ItemStack(Items.IRON_SWORD);
            this.dropStack(itemStack);
        }
        if (this.hasBackpack()){
            ItemStack itemStack = new ItemStack(Items.CHEST);
            this.dropStack(itemStack);
        }
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                this.dropStack(itemStack);
            }
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

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300;
        return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
    }
}
