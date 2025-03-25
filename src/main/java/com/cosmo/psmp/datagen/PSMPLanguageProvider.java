package com.cosmo.psmp.datagen;

import com.cosmo.psmp.entities.PSMPEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PSMPLanguageProvider extends FabricLanguageProvider {
    public PSMPLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(PSMPEntities.PUMPKIN_GUY,"Pumpkin Guy");
        translationBuilder.add(PSMPEntities.MELON_GUY,"Melon Guy");
    }
}
