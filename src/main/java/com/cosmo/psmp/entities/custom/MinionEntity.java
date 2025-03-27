package com.cosmo.psmp.entities.custom;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.behaviours.*;
import com.cosmo.psmp.networking.IntPayload;
import com.cosmo.psmp.screen.MinionScreenHandler;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
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
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
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
import java.util.function.IntFunction;

public class MinionEntity extends TameableEntity implements SmartBrainOwner<MinionEntity>,InventoryChangedListener,InventoryOwner, ExtendedScreenHandlerFactory<IntPayload> {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final SimpleInventory items = new SimpleInventory(14);

    private static final TrackedData<MinionEntity.State> STATE = DataTracker.registerData(MinionEntity.class, PSMP.MINION_STATE);
    private static final TrackedData<ItemStack> BACKPACK = DataTracker.registerData(MinionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> ARMOR = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> TOOL = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> OFFHAND = DataTracker.registerData(MinionEntity.class,TrackedDataHandlerRegistry.ITEM_STACK);

    public MinionEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        MinionEntity minionEntity = PSMPEntities.PUMPKIN_GUY.create(world);
        if (minionEntity != null && entity instanceof MinionEntity minionEntity1) {
            if (this.isTamed()) {
                minionEntity.setOwnerUuid(this.getOwnerUuid());
                minionEntity.setTamed(true, true);
            }
        }

        return minionEntity;
    }
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(other instanceof MinionEntity minionEntity)) {
            return false;
        } else {
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
            if (target instanceof WolfEntity wolfEntity) {
                return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
            } else {
                if (target instanceof PlayerEntity playerEntity) {
                    if (owner instanceof PlayerEntity playerEntity2) {
                        if (!playerEntity2.shouldDamagePlayer(playerEntity)) {
                            return false;
                        }
                    }
                }

                if (target instanceof AbstractHorseEntity abstractHorseEntity) {
                    if (abstractHorseEntity.isTame()) {
                        return false;
                    }
                }

                boolean var10000;
                if (target instanceof TameableEntity tameableEntity) {
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

    @Override
    protected void mobTick() {
        if(!(getState().equals(State.IDLE)||getState().equals(State.SITTING))) {
            tickBrain(this);
        }else{
            BrainUtils.clearMemories(brain, MemoryModuleType.ATTACK_TARGET);
        }
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
                        if(this.getState().equals(State.IDLE)){
                            player.sendMessage(Text.literal("Minion is now Sitting"),true);
                            this.setState(State.SITTING);
                        } else if (this.getState().equals(State.SITTING)) {
                            player.sendMessage(Text.literal("Minion is now Following"),true);
                            this.setState(State.FOLLOWING);
                        } else if (this.getState().equals(State.FOLLOWING)) {
                            player.sendMessage(Text.literal("Minion is now Wandering"),true);
                            this.setState(State.WANDERING);
                        } else if (this.getState().equals(State.WANDERING)) {
                            player.sendMessage(Text.literal("Minion is now Idle"),true);
                            this.setState(State.IDLE);
                        }
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

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STATE,State.FOLLOWING);
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

    public void setState(State state) {
        this.dataTracker.set(STATE,state);
    }

    public State getState() {
        return this.dataTracker.get(STATE);
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

    public boolean hasSeedToPlant() {
        return this.getInventory().containsAny(stack -> stack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS));
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
        nbt.putString("state", this.getState().asString());
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
        this.setState(MinionEntity.State.fromName(nbt.getString("state")));
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

    //deal with brain
    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MinionEntity>> getSensors() {
        return List.of(
                new NearbyItemsSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new NearbyPlayersSensor<>(),
                new NearestItemSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MinionEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new BreedWithPartner<>(),
                new LookAtTarget<>(),
                new MinionFollowOwner<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MinionEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new MinionFarmBehaviour<>(),
                new PickupItemBehaviour<>(),
                new FirstApplicableBehaviour<>(
                        new SetAttackTargetToOwnerAttackTarget<>(),
                        new SetAttackTargetToAttacker<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<MinionEntity>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(livingEntity -> livingEntity.getRandom().nextBetween(5,10))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends MinionEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)
        );
    }

    public static enum State implements StringIdentifiable {
        IDLE("idle", 0),
        SITTING("sitting", 1),
        FOLLOWING("following", 2),
        WANDERING("wandering", 3);

        private static final StringIdentifiable.EnumCodec<MinionEntity.State> CODEC = StringIdentifiable.createCodec(MinionEntity.State::values);
        private static final IntFunction<MinionEntity.State> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
                MinionEntity.State::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
        );
        public static final PacketCodec<ByteBuf, MinionEntity.State> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, MinionEntity.State::getIndex);
        private final String name;
        private final int index;

        State(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public static MinionEntity.State fromName(String name) {
            return (MinionEntity.State)CODEC.byId(name, IDLE);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private int getIndex() {
            return this.index;
        }
    }
}
