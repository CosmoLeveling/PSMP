package com.cosmo.psmp.items;

import com.cosmo.psmp.PSMP;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PSMPItemGroups {
    public static final Text ABILITY_TITLE = Text.translatable("itemgroup." + PSMP.MOD_ID + ".ability_group");
    public static final ItemGroup ABILITY_GROUP = register("ability_group", FabricItemGroup.builder()
            .displayName(ABILITY_TITLE)
            .icon(PSMPItems.ability_items.get(1)::getDefaultStack)
            .entries((displayContext, entries) -> {
                for(Item item : PSMPItems.ability_items){
                    entries.add(item);
                }
            })
            .build());

    public static <T extends ItemGroup> T register(String name, T itemgroup) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(PSMP.MOD_ID,name),itemgroup);
    }
    public static void init()
    {PSMP.LOGGER.info("registering ItemGroups");}
}
