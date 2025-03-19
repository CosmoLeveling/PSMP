package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SpawnMobPayload(BlockPos blockPos) implements CustomPayload {
    public static final CustomPayload.Id<SpawnMobPayload> ID = new CustomPayload.Id<>(Identifier.of(PSMP.MOD_ID,"spawn_mob_packet"));
    public static final PacketCodec<RegistryByteBuf, SpawnMobPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SpawnMobPayload::blockPos, SpawnMobPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
