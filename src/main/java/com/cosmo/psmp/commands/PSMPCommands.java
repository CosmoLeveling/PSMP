package com.cosmo.psmp.commands;

import com.cosmo.psmp.commands.arguments.AbilityArgumentType;
import com.cosmo.psmp.util.ModCustomAttachedData;
import com.cosmo.psmp.util.UnlockedAbilitiesAttachedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.cosmo.psmp.PSMP.MOD_ID;
import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;
import static com.cosmo.psmp.PSMPAttachmentTypes.UNLOCKED_ABILITIES;

public class PSMPCommands {
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID,"abilitytype"),AbilityArgumentType.class, ConstantArgumentSerializer.of(AbilityArgumentType::new));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("ability").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .then(CommandManager.argument("slot", IntegerArgumentType.integer(0,3))
                        .then(CommandManager.argument("ability", AbilityArgumentType.ability())
                                .executes(context -> {
                                    ModCustomAttachedData data = context.getSource().getPlayer().getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                                    context.getSource().getPlayer().setAttached(ABILITIES, data.setString(IntegerArgumentType.getInteger(context,"slot"),AbilityArgumentType.getAbility(context,"ability").asString()));
                                    context.getSource().sendFeedback(() -> Text.literal("Set Slot %s to %s".formatted(IntegerArgumentType.getInteger(context,"slot"),AbilityArgumentType.getAbility(context,"ability").asString())),false);
                                    return 1;
                                })))
                .then(CommandManager.argument("lock", AbilityArgumentType.ability())
                        .executes(
                                context -> {
                                    UnlockedAbilitiesAttachedData unlocked = context.getSource().getPlayer().getAttachedOrElse(UNLOCKED_ABILITIES,UnlockedAbilitiesAttachedData.DEFAULT);
                                    if (unlocked.stringList().contains(AbilityArgumentType.getAbility(context,"lock").asString())){
                                        context.getSource().getPlayer().setAttached(UNLOCKED_ABILITIES,unlocked.removeString(AbilityArgumentType.getAbility(context,"lock").asString()));
                                        context.getSource().sendFeedback(() -> Text.literal("locked %s".formatted(AbilityArgumentType.getAbility(context,"lock").asString())),false);
                                        return 1;
                                    }
                                    context.getSource().sendFeedback(() -> Text.literal("%s is locked".formatted(AbilityArgumentType.getAbility(context,"lock").asString())),false);
                                    return 0;
                                }
                        ))));
    }
}
