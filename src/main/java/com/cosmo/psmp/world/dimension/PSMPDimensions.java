package com.cosmo.psmp.world.dimension;

import com.cosmo.psmp.PSMP;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class PSMPDimensions {
    public static final RegistryKey<DimensionOptions> POCKET_DIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of(PSMP.MOD_ID,"pocket_dim"));
    public static final RegistryKey<World> POCKET_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of(PSMP.MOD_ID,"pocket_dim"));
    public static final RegistryKey<DimensionType> POCKET_DIM_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of(PSMP.MOD_ID,"pocket_dim_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(POCKET_DIM_TYPE_KEY, new DimensionType(
                OptionalLong.of(6000L),                                   // Fixed time (6000 is noon)
                true,                                    // has skylight
                false,                                   // Has ceiling
                false,                                   // Ultra warm (lava functionality in Nether)
                true,                                    // Natural dimension
                1.0,                                     // Coordinate scale (same scale as Overworld)
                true,                                    // piglin-safe condition
                false,                                 // Does not have respawn anchors
                -64,                                     // Minimum Y-level
                384,                                     // Height
                384,                                     // Logical height
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionTypes.OVERWORLD_ID,
                1.0f,
                new DimensionType.MonsterSettings(false,false, UniformIntProvider.create(0,0),0)
        ));
    }
}
