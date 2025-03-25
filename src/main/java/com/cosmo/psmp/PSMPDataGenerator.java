package com.cosmo.psmp;

import com.cosmo.psmp.datagen.PSMPLanguageProvider;
import com.cosmo.psmp.datagen.PSMPModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PSMPDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(PSMPModelProvider::new);
		pack.addProvider(PSMPLanguageProvider::new);
	}
}
