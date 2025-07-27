package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.PSMPEntities;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public record SpawnMobPayload(Boolean sneaking) implements CustomPayload {
    public static final CustomPayload.Id<SpawnMobPayload> ID = new CustomPayload.Id<>(Identifier.of(PSMP.MOD_ID,"spawn_mob_packet"));
    public static final PacketCodec<RegistryByteBuf, SpawnMobPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,SpawnMobPayload::sneaking, SpawnMobPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(PlayerEntity player) {
        if (Objects.equals(player.getMainHandStack().getItem(), Items.PUMPKIN)){
            PSMPEntities.PUMPKIN_GUY.spawn((ServerWorld) player.getWorld(), player.getBlockPos(),
                    SpawnReason.TRIGGERED).setOwner(player);
            player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
        }
        if (Objects.equals(player.getMainHandStack().getItem(),Items.MELON)){
            PSMPEntities.MELON_GUY.spawn((ServerWorld) player.getWorld(), player.getBlockPos(),
                    SpawnReason.TRIGGERED).setOwner(player);
            player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
        }
    }
}
