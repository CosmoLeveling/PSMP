package com.cosmo.psmp.datagen;

import com.cosmo.psmp.blocks.PSMPBlocks;
import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.cosmo.psmp.items.PSMPItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.minecraft.data.client.TextureMap.getId;

public class PSMPModelProvider extends FabricModelProvider {
    public PSMPModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(PSMPBlocks.VOID_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(PSMPBlocks.POCKET_PORTAL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for(Item item: PSMPItems.ability_items) {
            ability("generated",TextureKey.LAYER0).upload(ModelIds.getItemModelId(item), new TextureMap().put(TextureKey.LAYER0, Registries.ITEM.getId(item).withPrefixedPath("item/abilities/")), itemModelGenerator.writer);
        }
        itemModelGenerator.register(PSMPItems.Corrupt_Pearl,Models.GENERATED);
        itemModelGenerator.register(PSMPItems.Corrupt_Star,Models.GENERATED);
        itemModelGenerator.register(PSMPItems.Corrupt_Echo_Shard,Models.GENERATED);
    }
    private static Model ability(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }
}
