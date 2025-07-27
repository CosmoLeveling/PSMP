package com.cosmo.psmp.world;

import com.cosmo.psmp.PSMP;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;

public class PSMPPlacedFeatures {
    public static final RegistryKey<PlacedFeature> POCKET_DIM_PORTAL = registryKey("pocket_dim_portal_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeature = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        register(context,POCKET_DIM_PORTAL,configuredFeature.getOrThrow(PSMPConfiguredFeatures.POCKET_DIM_PORTAL));
    }

    public static RegistryKey<PlacedFeature> registryKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE,Identifier.of(PSMP.MOD_ID,name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?,?>> config
                                    ,List<PlacementModifier> modifiers) {
        context.register(key,new PlacedFeature(config, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> config,
                                                                                   PlacementModifier... modifiers) {
        register(context,key,config,List.of(modifiers));
    }
}
