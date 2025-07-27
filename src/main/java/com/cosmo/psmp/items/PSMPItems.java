package com.cosmo.psmp.items;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.cosmo.psmp.items.custom.AbilityItem;
import com.cosmo.psmp.items.custom.BAN;
import com.cosmo.psmp.items.custom.TestItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

public class PSMPItems {
    public static Item Corrupt_Pearl = registerItem("corrupt_pearl",new Item(new Item.Settings()));
    public static Item Test = registerItem("test",new TestItem(new Item.Settings()));
    public static Item Corrupt_Star = registerItem("corrupt_star",new Item(new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).fireproof()));
    public static Item Corrupt_Echo_Shard = registerItem("corrupt_echo_shard",new Item(new Item.Settings().rarity(Rarity.RARE)));
    public static Item Ban_Hammer = registerItem("ban_hammer", new BAN(new Item.Settings()));
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
