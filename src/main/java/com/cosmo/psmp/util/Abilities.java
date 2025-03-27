package com.cosmo.psmp.util;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.effects.PSMPEffects;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.networking.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;
import static com.cosmo.psmp.PSMPAttachmentTypes.TELEPORT_LOCATION_ATTACHMENT_TYPE;

public class Abilities {
    public static void register() {
        //Change Size
        ServerPlayNetworking.registerGlobalReceiver(ChangeSizePayload.ID, ((changesizePayload, context) -> {
            context.server().execute(() -> {
                if (context.player().isSneaking()) {
                    if (changesizePayload.mode().contains("reset_size")) {
                        ModCustomAttachedData data = context.player().getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                        context.player().setAttached(ABILITIES, data.setString(changesizePayload.slot(),"grow_size"));
                    } else if (changesizePayload.mode().contains("grow_size")) {
                        ModCustomAttachedData data = context.player().getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                        context.player().setAttached(ABILITIES, data.setString(changesizePayload.slot(),"shrink_size"));
                    } else {
                        ModCustomAttachedData data = context.player().getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                        context.player().setAttached(ABILITIES, data.setString(changesizePayload.slot(),"reset_size"));
                    }
                } else {
                    if (changesizePayload.mode().contains("reset_size")) {
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(1);
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
                    } else if (changesizePayload.mode().contains("grow_size")) {
                        double max_height=16;
                        for (int I = context.player().getBlockY();I<=context.player().getBlockY()+32;I++){
                            if(context.player().getWorld().getBlockState(context.player().getBlockPos().withY(I)).isAir()) {
                                max_height = ((double) (I - context.player().getBlockY()) / 2)+0.6;
                            }else{
                                break;
                            }
                        }
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() + 0.01, 0.325, max_height));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"),(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1)/4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_step"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    } else {
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() - 0.01,0.325,16));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"),(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1)/4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_step"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"),Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }
                }
            });
        }));

        //Summon Minion
        ServerPlayNetworking.registerGlobalReceiver(SpawnMobPayload.ID, ((spawnMobPayload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                if (Objects.equals(player.getMainHandStack().getItem(), Items.PUMPKIN)){
                    PSMPEntities.PUMPKIN_GUY.spawn((ServerWorld) context.player().getWorld(), context.player().getBlockPos(),
                            SpawnReason.TRIGGERED).setOwner(player);
                    player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
                }
                if (Objects.equals(player.getMainHandStack().getItem(),Items.MELON)){
                    PSMPEntities.MELON_GUY.spawn((ServerWorld) context.player().getWorld(), context.player().getBlockPos(),
                            SpawnReason.TRIGGERED).setOwner(player);
                    player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
                }
            });
        }));

        //Invisibility
        ServerPlayNetworking.registerGlobalReceiver(InvisibilityPayload.ID,(((invisibilityPayload, context) -> {
            context.server().execute(() -> {
                PlayerEntity entity = context.player();;
                entity.addStatusEffect(new StatusEffectInstance(PSMPEffects.TRUE_INVISIBILITY,3,1,false,false));
            });
        })));

        //Fertilize
        ServerPlayNetworking.registerGlobalReceiver(FertilizePayload.ID, (((fertilizePayload, context) -> {
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();
                Iterable<BlockPos> blocklist = BlockPos.iterateOutwards(pos,3,3,3);
                for (BlockPos block : blocklist) {
                    if (world instanceof ServerWorld && !world.getBlockState(block).isAir() && world.getBlockState(block).getBlock() instanceof Fertilizable fertilizable) {
                        if (fertilizable.canGrow(world, world.random, block, world.getBlockState(block))) {
                            fertilizable.grow((ServerWorld) world, world.random, block, world.getBlockState(block));
                        }
                    }
                }
            });
        })));
        //Warp
        ServerPlayNetworking.registerGlobalReceiver(WarpPayload.ID,(((warpPayload, context) ->
                context.server().execute(() -> {
                    PlayerEntity player = context.player();
                    TeleportLocationAttachedData teleportData = player.getAttachedOrElse(TELEPORT_LOCATION_ATTACHMENT_TYPE,new TeleportLocationAttachedData("none", new BlockPos(0,-300,0)));
                    if (!player.isSneaking()&& !teleportData.pos().equals(new BlockPos(0,-300,0))){
                        ServerWorld serverWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD,Identifier.of(teleportData.world())));
                        player.teleportTo(new TeleportTarget(serverWorld,teleportData.pos().toCenterPos(),player.getVelocity(),player.getYaw(),player.getPitch(), TeleportTarget.NO_OP));
                    }else{
                        player.setAttached(TELEPORT_LOCATION_ATTACHMENT_TYPE, new TeleportLocationAttachedData(player.getWorld().getDimensionEntry().getIdAsString(),player.getBlockPos()));
                    }
                }))));
    }
}
