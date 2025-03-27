package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InvisibilityPayload() implements CustomPayload {
    public static final Id<InvisibilityPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"phase_packet"));
    public static final PacketCodec<RegistryByteBuf, InvisibilityPayload> CODEC = PacketCodec.unit(new InvisibilityPayload());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
