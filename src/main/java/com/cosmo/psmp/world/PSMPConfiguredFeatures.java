package com.cosmo.psmp.world;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.world.features.PocketDimOutPortal;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;

public class PSMPConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?,?>> POCKET_DIM_PORTAL = registryKey("pocket_dim_portal");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context,POCKET_DIM_PORTAL,PSMP.PocketDimOutPortal,FeatureConfig.DEFAULT);
    }

    public static RegistryKey<ConfiguredFeature<?,?>> registryKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(PSMP.MOD_ID,name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature,config));
    }
}
