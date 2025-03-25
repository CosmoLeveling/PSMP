package com.cosmo.psmp.datagen;

import com.cosmo.psmp.items.PSMPItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class PSMPModelProvider extends FabricModelProvider {
    public PSMPModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(PSMPItems.fertilize, Models.GENERATED);
        itemModelGenerator.register(PSMPItems.summonMinion, Models.GENERATED);
    }
}
