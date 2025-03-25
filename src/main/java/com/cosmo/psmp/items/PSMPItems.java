package com.cosmo.psmp.items;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.items.custom.AbilityItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSMPItems {
    public static Item summonMinion = registerItem("summon_minion",new AbilityItem(new Item.Settings(),"summon_minion"));
    public static Item fertilize = registerItem("fertilize",new AbilityItem(new Item.Settings(),"fertilize"));
    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM,Identifier.of(PSMP.MOD_ID,name),item);
    }
    public static void registerItems(){
    }
}
