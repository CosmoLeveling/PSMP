package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.util.ModCustomAttachedData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;

public record ChangeSizePayload(String mode,int slot,Boolean sneaking) implements CustomPayload {
    public static final Id<ChangeSizePayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"change_size_packet"));
    public static final PacketCodec<RegistryByteBuf, ChangeSizePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING,ChangeSizePayload::mode,PacketCodecs.INTEGER,ChangeSizePayload::slot,PacketCodecs.BOOL,ChangeSizePayload::sneaking,ChangeSizePayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void handle(PlayerEntity player, ChangeSizePayload changesizePayload) {
        if (changesizePayload.sneaking()) {
            if (changesizePayload.mode().contains("reset_size")) {
                ModCustomAttachedData data = player.getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                player.setAttached(ABILITIES, data.setString(changesizePayload.slot(),"grow_size"));
            } else if (changesizePayload.mode().contains("grow_size")) {
                ModCustomAttachedData data = player.getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                player.setAttached(ABILITIES, data.setString(changesizePayload.slot(),"shrink_size"));
            } else {
                ModCustomAttachedData data = player.getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                player.setAttached(ABILITIES, data.setString(changesizePayload.slot(),"reset_size"));
            }
        } else {
            if (changesizePayload.mode().contains("reset_size")) {
                player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(1);
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
            } else if (changesizePayload.mode().contains("grow_size")) {
                double max_height=16;
                for (int I = player.getBlockY();I<=player.getBlockY()+32;I++){
                    if(player.getWorld().getBlockState(player.getBlockPos().withY(I)).isAir()) {
                        max_height = ((double) (I - player.getBlockY()) / 2)+0.6;
                    }else{
                        break;
                    }
                }
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
                player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() + 0.01, 0.325, max_height));
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"),(Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1)/4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_step"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            } else {
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"));
                player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).removeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"));
                player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"));
                player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"));
                player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(PSMP.MOD_ID,"size_step"));
                player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).removeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"));
                player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() - 0.01,0.325,16));
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_speed"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_jump"),(Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1)/4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_block_range"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_entity_range"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_step"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE).addTemporaryModifier(new EntityAttributeModifier(Identifier.of(PSMP.MOD_ID,"size_safe"),Math.clamp(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue(),1,16)-1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }
}
