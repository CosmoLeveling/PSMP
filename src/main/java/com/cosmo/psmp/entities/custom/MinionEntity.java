package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.networking.IntPayload;
import com.cosmo.psmp.screen.MinionScreenHandler;
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
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MinionEntity extends TameableEntity implements InventoryChangedListener,InventoryOwner, ExtendedScreenHandlerFactory<IntPayload> {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final SimpleInventory items = new SimpleInventory(14);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> BACKPACK = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> ARMOR = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> TOOL = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> OFFHAND = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);

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
                PSMP.LOGGER.info(itemStack1.toString());
                this.triggerItemPickedUpByEntityCriteria(item);
                this.sendPickup(item, itemStack1.getCount());
                this.items.addStack(itemStack1);
                itemStack.decrement(itemStack1.getCount());
                if (itemStack.isEmpty()) {
                    item.discard();
                }
            }
        }
        this.items.markDirty();
    }
    public boolean areInventoriesDifferent(Inventory inventory) {
        return this.items != inventory;
    }
    public ItemStack calculatePickedUpStack(ItemStack itemStack) {
        ItemStack returnedStack =ItemStack.EMPTY;
        if(items.isEmpty()){
            returnedStack = itemStack;
        }else{
            for (int s = 0; s < 9; s++) {
                ItemStack itemStack1 = items.getStack(s);
                if (itemStack1.isEmpty()) {
                    returnedStack = itemStack;
                } else if (itemStack1.isOf(itemStack.getItem())) {
                    if (itemStack1.getCount() != itemStack1.getMaxCount()) {
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
        if(!this.getWorld().isClient){
            this.dataTracker.set(TOOL,this.items.getStack(9));
            this.dataTracker.set(ARMOR,this.items.getStack(10));
            this.dataTracker.set(BACKPACK,this.items.getStack(12));
            this.dataTracker.set(OFFHAND,this.items.getStack(11));
        }
        this.setStackInHand(Hand.OFF_HAND, this.getOffhand());
        this.equipStack(EquipmentSlot.HEAD, this.getARMOR());
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

    public ItemStack getSwordSlot() {
        return this.items.getStack(9);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (player.shouldCancelInteraction()) {
            this.openInventory(player);
            return ActionResult.success(this.getWorld().isClient);
        } else if (this.getTool().isEmpty() &&( itemStack.isOf(Items.IRON_SWORD)||itemStack.isOf(Items.IRON_HOE)) && this.isOwner(player)) {
            this.setTool(itemStack.copy());
            itemStack.decrementUnlessCreative(1, player);
            return ActionResult.success(this.getWorld().isClient());
        } else if (this.getOffHandStack().isEmpty() && itemStack.isOf(Items.TOTEM_OF_UNDYING) && this.isOwner(player)) {
            this.items.setStack(11,itemStack.copy());
            itemStack.decrementUnlessCreative(1, player);
            return ActionResult.success(this.getWorld().isClient());
        } else if (this.getARMOR().isEmpty() && (itemStack.isOf(Items.CHAINMAIL_HELMET)||itemStack.isOf(Items.IRON_HELMET)||itemStack.isOf(Items.GOLDEN_HELMET)||itemStack.isOf(Items.LEATHER_HELMET)||itemStack.isOf(Items.DIAMOND_HELMET)||itemStack.isOf(Items.NETHERITE_HELMET)) && this.isOwner(player)) {
            this.setArmor(itemStack.copy());
            itemStack.decrementUnlessCreative(1, player);
            return ActionResult.success(this.getWorld().isClient());
        } else if (!this.hasBackpack() && itemStack.isOf(Items.CHEST) && this.isOwner(player)) {
            this.setBackpack(itemStack.copy(),1);
            itemStack.decrementUnlessCreative(1, player);
            return ActionResult.success(this.getWorld().isClient());
        } else if (!this.getWorld().isClient || this.isBaby() && this.isBreedingItem(itemStack)) {
            if (this.isTamed()) {
                if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    itemStack.decrementUnlessCreative(1, player);
                    FoodComponent foodComponent = (FoodComponent) itemStack.get(DataComponentTypes.FOOD);
                    float f = foodComponent != null ? (float) foodComponent.nutrition() : 1.0F;
                    this.heal(2.0F * f);
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
            this.items.markDirty();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        }
    }
    public void openInventory(PlayerEntity player) {
        if (!this.getWorld().isClient && this.isTamed() && this.isOwner(player)) {
            if(this.getWorld().getEntityById(getId()) instanceof MinionEntity minionEntity) {
                player.openHandledScreen(minionEntity);
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
        builder.add(SITTING,false);
        builder.add(BACKPACK,ItemStack.EMPTY);
        builder.add(OFFHAND,ItemStack.EMPTY);
        builder.add(ARMOR,ItemStack.EMPTY);
        builder.add(TOOL,ItemStack.EMPTY);
    }

    @Override
    public boolean tryAttack(Entity target) {
        return super.tryAttack(target);
    }

    public void setBackpack(ItemStack itemStack,int count) {
        this.items.setStack(12,new ItemStack(itemStack.getItem(),count));
        this.dataTracker.set(BACKPACK,new ItemStack(itemStack.getItem(),count));
        this.items.markDirty();
    }
    public boolean hasBackpack() {
        return (Boolean)this.dataTracker.get(BACKPACK).isOf(Items.CHEST);
    }
    public void setOffhand(ItemStack itemStack,int count) {
        this.items.setStack(12,new ItemStack(itemStack.getItem(),count));
        this.dataTracker.set(BACKPACK,new ItemStack(itemStack.getItem(),count));
        this.items.markDirty();
    }
    public ItemStack getOffhand() {
        return this.dataTracker.get(OFFHAND);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(SITTING,sitting);
    }

    public boolean isMobSitting() {
        return this.dataTracker.get(SITTING);
    }

    public ItemStack getARMOR() {
        ItemStack itemStack = this.dataTracker.get(ARMOR);
        if (itemStack.isOf(Items.LEATHER_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(6);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (itemStack.isOf(Items.CHAINMAIL_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(10);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (itemStack.isOf(Items.IRON_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(13);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (itemStack.isOf(Items.GOLDEN_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(9);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }else if (itemStack.isOf(Items.DIAMOND_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(17);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(6);
        }else if (itemStack.isOf(Items.NETHERITE_HELMET)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(17);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(9);
        }else{
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        }
        return itemStack;
    }
    public ItemStack getTool() {
        ItemStack itemStack = this.dataTracker.get(TOOL);
        if (itemStack.isOf(Items.IRON_SWORD)){
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)8.0F);
        }else{
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)1.0F);
        }
        return itemStack;
    }

    public Boolean hasSeedToPlant() {
        return this.items.containsAny(Set.of(Items.WHEAT_SEEDS));
    }

    public void setTool(ItemStack itemStack){
        this.items.setStack(9,itemStack);
        this.dataTracker.set(TOOL,itemStack);
        this.items.markDirty();
    }

    public void setArmor(ItemStack itemStack) {
        this.items.setStack(10,itemStack);
        this.dataTracker.set(ARMOR,itemStack);
        this.items.markDirty();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting",this.isMobSitting());
        if (!this.items.getStack(10).isEmpty()) {
            nbt.put("Helmet", this.items.getStack(10).encode(getRegistryManager()));
        }
        if (!this.items.getStack(12).isEmpty()) {
            nbt.put("Backpack", this.items.getStack(12).encode(getRegistryManager()));
        }
        if (!this.items.getStack(13).isEmpty()) {
            nbt.put("Offhand", this.items.getStack(13).encode(getRegistryManager()));
        }
        if (!this.items.getStack(9).isEmpty()) {
            nbt.put("Tool", this.items.getStack(9).encode(getRegistryManager()));
        }
        NbtList nbtList = new NbtList();
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = this.items.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)(i));
                nbtList.add(itemStack.encode(this.getRegistryManager(), nbtCompound));
                this.items.markDirty();
            }
        }

        nbt.put("Items", nbtList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING,nbt.getBoolean("isSitting"));
        if(nbt.contains("Helmet")) {
            this.items.setStack(10,ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Helmet")).orElse(ItemStack.EMPTY));
            this.dataTracker.set(ARMOR, ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Helmet")).orElse(ItemStack.EMPTY));
        } else {
            this.dataTracker.set(ARMOR,ItemStack.EMPTY);
        }
        if(nbt.contains("Backpack")) {
            this.items.setStack(12,ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Backpack")).orElse(ItemStack.EMPTY));
            this.dataTracker.set(BACKPACK, ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Backpack")).orElse(ItemStack.EMPTY));
        } else {
            this.dataTracker.set(BACKPACK,ItemStack.EMPTY);
        }
        if(nbt.contains("Offhand")) {
            this.items.setStack(13,ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Offhand")).orElse(ItemStack.EMPTY));
            this.dataTracker.set(OFFHAND, ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Offhand")).orElse(ItemStack.EMPTY));
        } else {
            this.dataTracker.set(OFFHAND,ItemStack.EMPTY);
        }
        if(nbt.contains("Tool")) {
            this.items.setStack(9,ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Tool")).orElse(ItemStack.EMPTY));
            this.dataTracker.set(TOOL, ItemStack.fromNbt(getRegistryManager(), nbt.getCompound("Tool")).orElse(ItemStack.EMPTY));
        } else {
            this.dataTracker.set(TOOL,ItemStack.EMPTY);
        }
        NbtList nbtList = nbt.getList("Items", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < 9) {
                this.items.setStack(j, (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
            }
        }
    }

    @Override
    protected void dropInventory() {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack = this.items.getStack(i);
            if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                this.dropStack(itemStack);
            }
        }
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        setSitting(false);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.items;
    }
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300;
        return i >= 0 && i < this.items.size() ? StackReference.of(this.items, i) : super.getStackReference(mappedIndex);
    }

    @Override
    public IntPayload getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return new IntPayload(this.getId());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MinionScreenHandler(syncId,playerInventory,this);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        if (!this.getWorld().isClient) {
            this.dataTracker.set(ARMOR,sender.getStack(10));
        }
    }
}
