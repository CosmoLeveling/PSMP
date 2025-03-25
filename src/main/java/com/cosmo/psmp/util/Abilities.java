package com.cosmo.psmp.util;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.effects.PSMPEffects;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.networking.ChangeSizePayload;
import com.cosmo.psmp.networking.FertilizePayload;
import com.cosmo.psmp.networking.PhasePayload;
import com.cosmo.psmp.networking.SpawnMobPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;

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
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size"),16, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"),16, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    } else if (changesizePayload.mode().contains("grow_size")) {
                        double max_height=16;
                        for (int I = context.player().getBlockY();I<=context.player().getBlockY()+32;I++){
                            if(context.player().getWorld().getBlockState(context.player().getBlockPos().withY(I)).isAir()) {
                                max_height = ((double) (I - context.player().getBlockY()) / 2)+0.6;
                            }else{
                                break;
                            }
                        }
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() + 0.01, 0.45, max_height));
                    } else {
                        context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() - 0.01,0.45,16));
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

        //Phase
        ServerPlayNetworking.registerGlobalReceiver(PhasePayload.ID,(((phasePayload, context) -> {
            context.server().execute(() -> {
                PlayerEntity entity = context.player();;
                entity.addStatusEffect(new StatusEffectInstance(PSMPEffects.PHASE,3,1,false,false));
            });
        })));

        //Fertilize
        ServerPlayNetworking.registerGlobalReceiver(FertilizePayload.ID, (((fertilizePayload, context) -> {
            context.server().execute(() -> {
                PSMP.LOGGER.info("Start");
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
                PSMP.LOGGER.info("End");
            });
        })));
    }
}
