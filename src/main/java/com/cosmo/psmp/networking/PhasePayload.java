package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PhasePayload(Boolean phase) implements CustomPayload {
    public static final Id<PhasePayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"phase_packet"));
    public static final PacketCodec<RegistryByteBuf, PhasePayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, PhasePayload::phase, PhasePayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
