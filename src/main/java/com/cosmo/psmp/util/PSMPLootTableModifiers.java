package com.cosmo.psmp.util;

import com.cosmo.psmp.items.PSMPItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class PSMPLootTableModifiers {

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register(((registryKey, builder, lootTableSource, wrapperLookup) -> {
            if (LootTables.VILLAGE_DESERT_HOUSE_CHEST.equals(registryKey)||LootTables.VILLAGE_SAVANNA_HOUSE_CHEST.equals(registryKey)||LootTables.VILLAGE_PLAINS_CHEST.equals(registryKey)||LootTables.VILLAGE_TAIGA_HOUSE_CHEST.equals(registryKey)||LootTables.VILLAGE_SNOWY_HOUSE_CHEST.equals(registryKey)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(PSMPItems.ability_items.get(2)))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f,2.0f)).build());

                builder.pool(poolBuilder);
            }
        }));
    }
}
