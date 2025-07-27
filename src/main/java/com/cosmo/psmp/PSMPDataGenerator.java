package com.cosmo.psmp;

import com.cosmo.psmp.datagen.PSMPLanguageProvider;
import com.cosmo.psmp.datagen.PSMPModelProvider;
import com.cosmo.psmp.datagen.PSMPWorldGenerator;
import com.cosmo.psmp.world.PSMPConfiguredFeatures;
import com.cosmo.psmp.world.PSMPPlacedFeatures;
import com.cosmo.psmp.world.dimension.PSMPDimensions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class PSMPDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(PSMPModelProvider::new);
		pack.addProvider(PSMPLanguageProvider::new);
		pack.addProvider(PSMPWorldGenerator::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, PSMPDimensions::bootstrapType);
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, PSMPConfiguredFeatures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PSMPPlacedFeatures::bootstrap);
	}
}
