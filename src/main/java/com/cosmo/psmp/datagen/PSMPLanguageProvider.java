package com.cosmo.psmp.datagen;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.items.PSMPItemGroups;
import com.cosmo.psmp.items.PSMPItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class PSMPLanguageProvider extends FabricLanguageProvider {
    public PSMPLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(PSMPEntities.PUMPKIN_GUY,"Pumpkin Guy");
        translationBuilder.add(PSMPEntities.MELON_GUY,"Melon Guy");
        for(AbilityEnum ability: AbilityEnum.values()){
            for(Item item: PSMPItems.ability_items) {
                if (item.getTranslationKey().contains(ability.asString())) {
                    translationBuilder.add(item.getTranslationKey(), ability.getName());
                }
            }
        }
        translationBuilder.add(Identifier.of("itemgroup", "psmp.ability_group"),"Abilities");
    }
}
