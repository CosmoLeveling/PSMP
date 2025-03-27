package com.cosmo.psmp.entities.behaviours;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.custom.MinionEntity;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.*;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class MinionFarmBehaviour<T extends MinionEntity> extends ExtendedBehaviour<T> {
    private static final List<Pair<MemoryModuleType<?>, MemoryModuleState>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.WALK_TARGET,MemoryModuleState.VALUE_PRESENT));
    protected BiPredicate<T, Vec3d> positionPredicate = (entity, pos) -> true;
    protected BiFunction<T, Vec3d, Float> speedModifier = (entity, targetPos) -> 1.0F;
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
    private final List<BlockPos> targetPositions = Lists.<BlockPos>newArrayList();
    @Override
    protected boolean doStartCheck(ServerWorld level, T entity, long gameTime) {
        if(entity.getTool().isOf(Items.IRON_HOE)) {
            BlockPos.Mutable mutable = entity.getBlockPos().mutableCopy();
            this.targetPositions.clear();
            for (int i = -3; i <= 3; i++) {
                for (int j = -3; j <= 3; j++) {
                    for (int k = -3; k <= 3; k++) {
                        mutable.set(entity.getX() + i, entity.getY() + j, entity.getZ() + k);
                        if (this.isSuitableTarget(mutable, level, entity)) {
                            this.targetPositions.add(new BlockPos(mutable));
                        }
                    }
                }
            }
            this.currentTarget = this.chooseRandomTarget(level);
            return this.currentTarget != null;
        }else{
            return false;
        }
    }
    @Nullable
    private BlockPos chooseRandomTarget(ServerWorld world) {
        return this.targetPositions.isEmpty() ? null : (BlockPos)this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }

    private boolean isSuitableTarget(BlockPos pos, ServerWorld world, MinionEntity entity) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        if (block2 instanceof FarmlandBlock && blockState.isAir() && !entity.hasSeedToPlant()){
            return false;
        }
        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) || blockState.isAir() && block2 instanceof FarmlandBlock;
    }
    @Override
    protected void start(T entity) {
        if(currentTarget!=null) {
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(currentTarget, (Float) this.speedModifier.apply(entity, Vec3d.of(currentTarget)), 0));
        } else {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        }
    }

    @Override
    protected void stop(T entity) {
        World serverWorld = entity.getWorld();
        if(currentTarget!=null) {
            BlockState blockState = serverWorld.getBlockState(this.currentTarget);
            Block block = blockState.getBlock();
            Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
            if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
                serverWorld.breakBlock(this.currentTarget, true, entity);
            }
            if (blockState.isAir() && block2 instanceof FarmlandBlock && entity.hasSeedToPlant()) {
                SimpleInventory simpleInventory = entity.getInventory();

                for (int i = 0; i < simpleInventory.size(); i++) {
                    ItemStack itemStack = simpleInventory.getStack(i);
                    boolean bl = false;
                    if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS) && itemStack.getItem() instanceof BlockItem blockItem) {
                        BlockState blockState2 = blockItem.getBlock().getDefaultState();
                        serverWorld.setBlockState(this.currentTarget, blockState2);
                        serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, this.currentTarget, GameEvent.Emitter.of(entity, blockState2));
                        bl = true;
                    }

                    if (bl) {
                        serverWorld.playSound(
                                null, this.currentTarget.getX(), this.currentTarget.getY(), this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F
                        );
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            simpleInventory.setStack(i, ItemStack.EMPTY);
                        }
                        break;
                    }
                }
            }
        }
        entity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        super.stop(entity);
    }

    @Override
    protected void keepRunning(ServerWorld level, T entity, long gameTime) {
        super.keepRunning(level, entity, gameTime);
        this.ticksRan++;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld level, T entity, long gameTime) {
        return this.ticksRan < 10;
    }
}
