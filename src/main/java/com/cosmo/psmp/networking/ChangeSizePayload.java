package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ChangeSizePayload(String mode,int slot) implements CustomPayload {
    public static final Id<ChangeSizePayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"change_size_packet"));
    public static final PacketCodec<RegistryByteBuf, ChangeSizePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING,ChangeSizePayload::mode,PacketCodecs.INTEGER,ChangeSizePayload::slot,ChangeSizePayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
