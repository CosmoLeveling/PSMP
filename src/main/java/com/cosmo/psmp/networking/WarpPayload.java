package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record WarpPayload() implements CustomPayload {
    public static final WarpPayload INSTANCE = new WarpPayload();
    public static final PacketCodec<RegistryByteBuf, WarpPayload> CODEC = PacketCodec.unit(INSTANCE);
    public static final Id<WarpPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"warp_packet"));
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
