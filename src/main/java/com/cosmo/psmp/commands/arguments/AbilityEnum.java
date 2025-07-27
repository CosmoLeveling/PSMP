package com.cosmo.psmp.commands.arguments;

import com.cosmo.psmp.items.PSMPItems;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.StringIdentifiable;

import java.util.List;

public enum AbilityEnum implements StringIdentifiable {
    None("None","None",0, List.of(), 0),
    summon_minion("summon_minion","Summon Minion",10, List.of(), 3),
    change_size("grow_size", "Grow Size",0, List.of(), 1),
    fertilize("fertilize", "Fertilize",5, List.of(), 1),
    invisibility("invisibility", "Invisibility",0, List.of(), 2),
    copy("copy","Copy",60, List.of(new ItemStack(PSMPItems.Corrupt_Star,5),new ItemStack(PSMPItems.Corrupt_Echo_Shard,5)), 4),
    warp("warp", "Warp",25, List.of(new ItemStack(Items.BONE_MEAL,32),new ItemStack(Items.GOLDEN_APPLE)), 2),
    pocket_dimension("pocket_dimension", "Pocket Dimension",40, List.of(), 3);

    public static final Codec<AbilityEnum> CODEC = StringIdentifiable.createCodec(AbilityEnum::values);
    private final String id;
    private final String ability;
    private final Integer cooldown;
    private final List<ItemStack> items_to_unlock;
    private final Integer Exp_Needed_to_unlock;
    private AbilityEnum(final String id, String ability, Integer cooldown, List<ItemStack> itemsToUnlock, Integer expNeededToUnlock) {
        this.id = id;
        this.ability = ability;
        this.cooldown = cooldown;
        this.items_to_unlock = itemsToUnlock;
        this.Exp_Needed_to_unlock = expNeededToUnlock;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Integer getCooldown(){
        return this.cooldown;
    }

    public List<ItemStack> getItems_to_unlock() {
        return this.items_to_unlock;
    }

    public String getName(){
        return this.ability;
    }
}
