package com.cosmo.psmp.blocks.entity.custom;

import com.cosmo.psmp.blocks.entity.PSMPBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class CopyBlockEntity extends BlockEntity {
    public CopyBlockEntity(BlockPos pos, BlockState state) {
        super(PSMPBlockEntities.COPY_BE, pos, state);
    }
}
