package com.cosmo.psmp.event;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.PSMPAttachmentTypes;
import com.cosmo.psmp.commands.arguments.AbilityEnum;
import com.cosmo.psmp.networking.*;
import com.cosmo.psmp.util.ModCustomAttachedData;
import com.cosmo.psmp.util.UnlockedAbilitiesAttachedData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;


public class KeyInputHandler {
    private static KeyBinding Ability1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.ability1",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "category.examplemod.test"
    ));
    private static KeyBinding Ability2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.ability2",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.examplemod.test"
    ));
    private static KeyBinding Ability3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.ability3",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.examplemod.test"
    ));
    private static KeyBinding Ability4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.ability4",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.examplemod.test"
    ));
    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.player==null) return;
            ModCustomAttachedData data = client.player.getAttachedOrElse(PSMPAttachmentTypes.ABILITIES,ModCustomAttachedData.DEFAULT);
            UnlockedAbilitiesAttachedData unlocked = client.player.getAttachedOrElse(PSMPAttachmentTypes.UNLOCKED_ABILITIES,UnlockedAbilitiesAttachedData.DEFAULT);
            if (data.Cooldowns().get(0) > 0) {
                data = data.setCooldown(0, data.Cooldowns().get(0) - 1);
                client.player.setAttached(PSMPAttachmentTypes.ABILITIES, data);
            }
            if (data.Cooldowns().get(1) > 0) {
                data = data.setCooldown(1, data.Cooldowns().get(1) - 1);
                client.player.setAttached(PSMPAttachmentTypes.ABILITIES, data);
            }
            if (data.Cooldowns().get(2) > 0) {
                data = data.setCooldown(2, data.Cooldowns().get(2) - 1);
                client.player.setAttached(PSMPAttachmentTypes.ABILITIES, data);
            }
            if (data.Cooldowns().get(3) > 0) {
                data = data.setCooldown(3, data.Cooldowns().get(3) - 1);
                client.player.setAttached(PSMPAttachmentTypes.ABILITIES, data);
            }
            if (client.player!= null) {
                if (client.player.input.sneaking) {
                    while (Ability1.wasPressed() && data.Cooldowns().get(0).equals(0)) {
                        if (unlocked.stringList().contains(data.stringList().get(0))) {
                            cast_ability(0, client, true);
                        }else{
                            try_unlock_ability(data.stringList().get(0),client);
                        }
                    }
                    while (Ability2.wasPressed() && data.Cooldowns().get(1).equals(0)) {
                        cast_ability(1, client,true);
                    }
                    while (Ability3.wasPressed() && data.Cooldowns().get(2).equals(0)) {
                        cast_ability(2, client, true);
                    }
                    while (Ability4.wasPressed() && data.Cooldowns().get(3).equals(0)) {
                        cast_ability(3, client, true);
                    }
                }else{
                    if (Ability1.isPressed() && data.Cooldowns().get(0).equals(0)) {
                        if (unlocked.stringList().contains(data.stringList().get(0))) {
                            cast_ability(0, client, false);
                        }else{
                            try_unlock_ability(data.stringList().get(0),client);
                        }
                    }
                    if (Ability2.isPressed() && data.Cooldowns().get(1).equals(0)) {
                        cast_ability(1, client, false);
                    }
                    if (Ability3.isPressed() && data.Cooldowns().get(2).equals(0)) {
                        cast_ability(2, client, false);
                    }
                    if (Ability4.isPressed() && data.Cooldowns().get(3).equals(0)) {
                        cast_ability(3, client, false);
                    }
                }
            }
        });
    }

    private static void try_unlock_ability(String s, MinecraftClient client) {
        boolean unlock = true;
        UnlockedAbilitiesAttachedData unlocked = client.player.getAttachedOrElse(PSMPAttachmentTypes.UNLOCKED_ABILITIES,UnlockedAbilitiesAttachedData.DEFAULT);
        for (ItemStack itemStack:AbilityEnum.change_size.getItems_to_unlock()) {
            PSMP.LOGGER.info("Checking for item: " + itemStack.toString() );
            if (!client.player.getInventory().contains(itemStack)) {
                PSMP.LOGGER.info("Missing item to unlock ability: " + s);
                unlock = false;
            }
        }
        if(unlock){
            client.player.setAttached(PSMPAttachmentTypes.UNLOCKED_ABILITIES,unlocked.addString(s));
        }
    }

    private static void cast_ability(Integer slot, MinecraftClient client,boolean sneaking) {
        if(client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("copy")) {
            if (sneaking) {
                ModCustomAttachedData data = client.player.getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                if (data.stringList().get(slot).contains("size")){
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(1);
                }
                client.player.setAttached(ABILITIES, data.setString(slot,"copy").setCooldown(slot,AbilityEnum.copy.getCooldown()));
            }else{
                if (client.player.getAttached(ABILITIES).stringList().get(slot).equals("copy")) {
                    if (client.targetedEntity instanceof PlayerEntity) {
                        ModCustomAttachedData data = client.player.getAttachedOrElse(ABILITIES, ModCustomAttachedData.DEFAULT);
                        client.player.setAttached(ABILITIES, data.setString(slot, client.targetedEntity.getAttached(ABILITIES).stringList().get(0) + "_copy"));
                    }
                } else {
                    chooseAblilty(client,slot,sneaking);
                }
            }
        }else{
            chooseAblilty(client,slot, sneaking);
        }
    }
    private static void chooseAblilty(MinecraftClient client,Integer slot,Boolean sneaking) {
        if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("summon_minion")) {
            ClientPlayNetworking.send(new SpawnMobPayload(sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot,AbilityEnum.summon_minion.getCooldown());
            client.player.setAttached(ABILITIES, data);
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("size")) {
            ClientPlayNetworking.send(new ChangeSizePayload(client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot), slot,sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot , AbilityEnum.change_size.getCooldown() );
            client.player.setAttached(ABILITIES, data);
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("fertilize")) {
            ClientPlayNetworking.send(new FertilizePayload(sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot,AbilityEnum.fertilize.getCooldown());
            client.player.setAttached(ABILITIES, data);
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("invisibility")) {
            ClientPlayNetworking.send(new InvisibilityPayload(sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot,AbilityEnum.invisibility.getCooldown());
            client.player.setAttached(ABILITIES, data);
        } else if (client.player.getAttached(ABILITIES).stringList().get(slot).contains("warp")) {
            ClientPlayNetworking.send(new WarpPayload(sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot,AbilityEnum.warp.getCooldown());
            client.player.setAttached(ABILITIES, data);
        } else if (client.player.getAttached(ABILITIES).stringList().get(slot).contains(AbilityEnum.pocket_dimension.asString())) {
            ClientPlayNetworking.send(new PocketDimPayload(sneaking));
            ModCustomAttachedData data = client.player.getAttached(PSMPAttachmentTypes.ABILITIES);
            data = data.setCooldown(slot,AbilityEnum.pocket_dimension.getCooldown());
            client.player.setAttached(ABILITIES, data);
        }

    }
}
