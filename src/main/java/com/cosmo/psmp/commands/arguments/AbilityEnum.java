package com.cosmo.psmp.commands.arguments;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum AbilityEnum implements StringIdentifiable {
    None("None","None"),
    summon_minion("summon_minion","Summon Minion"),
    change_size("grow_size", "Grow Size"),
    fertilize("fertilize", "Fertilize"),
    phase("phase", "Phase"),
    copy("copy","Copy"),
    warp("warp", "Warp");

    public static final Codec<AbilityEnum> CODEC = StringIdentifiable.createCodec(AbilityEnum::values);
    private final String id;
    private final String ability;
    private AbilityEnum(final String id, String ability) {
        this.id = id;
        this.ability = ability;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public String getName(){
        return this.ability;
    }
}
