package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record IntPayload(int id) implements CustomPayload {
    public static final Id<IntPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"uuid"));

    public static final PacketCodec<RegistryByteBuf, IntPayload> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, IntPayload::id, IntPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
