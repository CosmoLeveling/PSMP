package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record FertilizePayload(Boolean sneaking) implements CustomPayload {
    public static final Id<FertilizePayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"fertilize_packet"));
    public static final PacketCodec<RegistryByteBuf, FertilizePayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,FertilizePayload::sneaking,FertilizePayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void handle (PlayerEntity player) {
        World world = player.getWorld();
        BlockPos pos = player.getBlockPos();
        Iterable<BlockPos> blocklist = BlockPos.iterateOutwards(pos,3,3,3);
        for (BlockPos block : blocklist) {
            if (world instanceof ServerWorld && !world.getBlockState(block).isAir() && world.getBlockState(block).getBlock() instanceof Fertilizable fertilizable) {
                if (fertilizable.canGrow(world, world.random, block, world.getBlockState(block))) {
                    fertilizable.grow((ServerWorld) world, world.random, block, world.getBlockState(block));
                }
            }
        }
    }
}
