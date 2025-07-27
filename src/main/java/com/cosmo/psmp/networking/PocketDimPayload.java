package com.cosmo.psmp.networking;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.blocks.PSMPBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record PocketDimPayload(Boolean sneaking) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, PocketDimPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, PocketDimPayload::sneaking, PocketDimPayload::new);
    public static final Id<PocketDimPayload> ID = new Id<>(Identifier.of(PSMP.MOD_ID,"pocket_dim_packet"));
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(PlayerEntity player) {
        World world = player.getWorld();
        BlockHitResult target = (BlockHitResult) player.raycast(5.0D, 0, false); // Get the block position the player is looking at

        BlockState blockState = world.getBlockState(target.getBlockPos());
        ShapeContext shapeContext = ShapeContext.of(player);
        if (blockState.isOf(PSMPBlocks.POCKET_PORTAL)) {
            world.setBlockState(target.getBlockPos(), Blocks.AIR.getDefaultState());
        } else {
            if ((PSMPBlocks.POCKET_PORTAL.getDefaultState().canPlaceAt(world, target.getBlockPos())) && world.canPlace(PSMPBlocks.POCKET_PORTAL.getDefaultState(), target.getBlockPos(), shapeContext)){
                BlockPos offsetPos = target.getBlockPos().offset(target.getSide());
                world.setBlockState(offsetPos, PSMPBlocks.POCKET_PORTAL.getDefaultState());
            }
        }
    }
}
