package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FertilizePayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<FertilizePayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"fertilize_packet"));
    public static final PacketCodec<RegistryByteBuf, FertilizePayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC,FertilizePayload::blockPos,FertilizePayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
