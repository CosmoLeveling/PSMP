package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.effects.PSMPEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InvisibilityPayload(Boolean sneaking) implements CustomPayload {
    public static final Id<InvisibilityPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"phase_packet"));
    public static final PacketCodec<RegistryByteBuf, InvisibilityPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,InvisibilityPayload::sneaking,InvisibilityPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void handle(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(PSMPEffects.TRUE_INVISIBILITY,3,1,false,false));
    }
}
