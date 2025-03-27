package com.cosmo.psmp.blocks.entity;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.blocks.PSMPBlocks;
import com.cosmo.psmp.blocks.entity.custom.CopyBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSMPBlockEntities {
    public static final BlockEntityType<CopyBlockEntity> COPY_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(PSMP.MOD_ID,"copy_be"),
                    BlockEntityType.Builder.create(CopyBlockEntity::new, PSMPBlocks.COPY_BLOCK).build());

    public void init() {
    }
}
