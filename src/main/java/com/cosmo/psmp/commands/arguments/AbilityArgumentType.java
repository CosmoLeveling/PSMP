package com.cosmo.psmp.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class AbilityArgumentType extends EnumArgumentType<AbilityEnum> {
    public AbilityArgumentType() {
        super(AbilityEnum.CODEC, AbilityEnum::values);
    }
    public static AbilityArgumentType ability() {
        return new AbilityArgumentType();
    }

    public static AbilityEnum getAbility(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, AbilityEnum.class);
    }
}
