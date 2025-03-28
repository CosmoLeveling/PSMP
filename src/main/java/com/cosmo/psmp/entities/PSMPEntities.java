package com.cosmo.psmp.entities;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSMPEntities {
    public static final EntityType<MinionEntity> PUMPKIN_GUY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PSMP.MOD_ID,"pumpkin_guy"),
            EntityType.Builder.create(MinionEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f,0.5f).build());
    public static final EntityType<MinionEntity> MELON_GUY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PSMP.MOD_ID,"melon_guy"),
            EntityType.Builder.create(MinionEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f,0.5f).build());
    public static void registerEntities() {
    }
}
