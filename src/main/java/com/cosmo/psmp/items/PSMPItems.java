package com.cosmo.psmp.items;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.cosmo.psmp.items.custom.AbilityItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PSMPItems {
    public static List<Item> ability_items = registerAbilities();
    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM,Identifier.of(PSMP.MOD_ID,name),item);
    }
    public static List<Item> registerAbilities(){
        List<Item> abilities = new ArrayList<>();
        for(AbilityEnum ability:AbilityEnum.values()){
            if (!ability.asString().equals("None")) {
                Item item = registerItem(ability.asString(), new AbilityItem(new Item.Settings(), ability.asString()));
                abilities.add(item);
            }
        }
        return abilities;
    }
    public static void registerItems(){
    }
}
