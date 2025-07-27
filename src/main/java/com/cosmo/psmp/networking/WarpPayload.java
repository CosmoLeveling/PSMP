package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.util.TeleportLocationAttachedData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

import static com.cosmo.psmp.PSMPAttachmentTypes.TELEPORT_LOCATION_ATTACHMENT_TYPE;

public record WarpPayload(Boolean sneaking) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, WarpPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,WarpPayload::sneaking,WarpPayload::new);
    public static final Id<WarpPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"warp_packet"));
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void handle(PlayerEntity player, WarpPayload warpPayload){
        TeleportLocationAttachedData teleportData = player.getAttachedOrElse(TELEPORT_LOCATION_ATTACHMENT_TYPE,new TeleportLocationAttachedData("none", new BlockPos(0,-300,0)));
        if (warpPayload.sneaking() || teleportData.pos().equals(new BlockPos(0, -300, 0))) {
            player.setAttached(TELEPORT_LOCATION_ATTACHMENT_TYPE, new TeleportLocationAttachedData(player.getWorld().getDimensionEntry().getIdAsString(),player.getBlockPos()));
        } else {
            ServerWorld serverWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD,Identifier.of(teleportData.world())));
            player.teleportTo(new TeleportTarget(serverWorld,teleportData.pos().toCenterPos().subtract(0,0.5,0),player.getVelocity(),player.getYaw(),player.getPitch(), TeleportTarget.NO_OP));
        }
    }
}
