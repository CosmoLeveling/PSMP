package com.cosmo.psmp.blocks;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.blocks.custom.CopyBlock;
import com.cosmo.psmp.blocks.custom.PocketPortal;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSMPBlocks {
    public static Block POCKET_PORTAL = registerBlock("pocket_portal",new PocketPortal(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)
            .strength(-1.0F, 3600000.0F)
            .dropsNothing()
            .allowsSpawning(Blocks::never)));
    public static Block COPY_BLOCK = registerBlock("test",new CopyBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static Block VOID_BLOCK = registerBlock("void_block",new Block(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)));
    public static Block registerBlock(String name, Block block) {
        Block block1 = Registry.register(Registries.BLOCK, Identifier.of(PSMP.MOD_ID,name),block);
        registerBlockItem(name,block1);
        return block1;
    }
    public static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM,Identifier.of(PSMP.MOD_ID,name),new BlockItem(block,new Item.Settings()));
    }

    public static void init(){}
}
