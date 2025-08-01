package com.cosmo.psmp.util;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.items.PSMPItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class PSMPLootTableModifiers {
    public static final Identifier ENDERMAN_ENTIY
            = Identifier.of("minecraft","entities/enderman");
    public static final Identifier WITHER_ENTIY
        = Identifier.of("minecraft","entities/wither");
    public static final Identifier WARDEN_ENTIY
        = Identifier.of("minecraft","entities/warden");
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
            if (ENDERMAN_ENTIY.equals(registryKey.getValue())){
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.05f))
                        .with(ItemEntry.builder(PSMPItems.Corrupt_Pearl))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f,2.0f)).build());
                builder.pool(poolBuilder);
            }
            if (WITHER_ENTIY.equals(registryKey.getValue())){
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(PSMPItems.Corrupt_Star))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f,2.0f)).build());
                builder.pool(poolBuilder);
            }
            if (WARDEN_ENTIY.equals(registryKey.getValue())){
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.35f))
                        .with(ItemEntry.builder(PSMPItems.Corrupt_Echo_Shard))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f,2.0f)).build());
                builder.pool(poolBuilder);
            }
        }));
    }
}
