package com.cosmo.psmp.blocks.custom;

import com.cosmo.psmp.world.dimension.PSMPDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

public class PocketPortal extends Block {
    public PocketPortal(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                ServerWorld serverWorld;
                BlockPos teleportPos = null;
                if (world.getRegistryKey().equals(PSMPDimensions.POCKET_WORLD_KEY)) {

                    if (serverPlayerEntity.getSpawnPointPosition() == null||serverPlayerEntity.getSpawnPointDimension() == null || serverPlayerEntity.getSpawnPointDimension().equals(PSMPDimensions.POCKET_WORLD_KEY)) {
                        // Fallback to Overworld if spawn point is null or located in the Pocket Dimension
                        serverWorld = world.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.of("overworld")));
                        teleportPos = BlockPos.ofFloored(Vec3d.ofCenter(serverWorld.getSpawnPos()));
                    } else {
                        serverWorld = player.getServer().getWorld(serverPlayerEntity.getSpawnPointDimension());
                        teleportPos = serverPlayerEntity.getSpawnPointPosition();
                    }

                    if (serverWorld != null && teleportPos != null) {
                        player.teleportTo(new TeleportTarget(serverWorld, Vec3d.ofCenter(teleportPos).subtract(0, 0.5, 0), player.getVelocity(), player.getYaw(), player.getPitch(), TeleportTarget.NO_OP));
                    }
                } else {
                    serverWorld = player.getServer().getWorld(PSMPDimensions.POCKET_WORLD_KEY);
                    if (serverWorld != null) {
                        player.teleportTo(new TeleportTarget(serverWorld, pos.toCenterPos().add(0, 0.5, 0), player.getVelocity(), player.getYaw(), player.getPitch(), TeleportTarget.NO_OP));
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

}
