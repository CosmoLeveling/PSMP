package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record PhaseUpdateS2CPacket(UUID entity) implements CustomPayload {
    public static final CustomPayload.Id<PhaseUpdateS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(PSMP.MOD_ID,"phase_c_packet"));
    public static final PacketCodec<RegistryByteBuf, PhaseUpdateS2CPacket> CODEC = PacketCodec.tuple(Uuids.PACKET_CODEC, PhaseUpdateS2CPacket::entity, PhaseUpdateS2CPacket::new);
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
