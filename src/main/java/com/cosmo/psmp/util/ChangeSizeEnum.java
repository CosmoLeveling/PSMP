package com.cosmo.psmp.util;

import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum ChangeSizeEnum implements StringIdentifiable{
    Reset("reset_size"),
    Grow("grow_size"),
    Shrink("shrink_size");
    private final String id;
    public static final Codec<ChangeSizeEnum> CODEC = StringIdentifiable.createCodec(ChangeSizeEnum::values);
    private ChangeSizeEnum(final String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
