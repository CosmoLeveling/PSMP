package com.cosmo.psmp.commands.arguments;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum AbilityEnum implements StringIdentifiable {
    None("None"),
    summon_minion("summon_minion"),
    change_size("grow_size"),
    fertilize("fertilize"),
    phase("phase"),
    copy("copy");

    public static final Codec<AbilityEnum> CODEC = StringIdentifiable.createCodec(AbilityEnum::values);
    private final String id;
    private AbilityEnum(final String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
