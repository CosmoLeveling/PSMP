package com.cosmo.psmp.event;

import com.cosmo.psmp.PSMPAttachmentTypes;
import com.cosmo.psmp.networking.*;
import com.cosmo.psmp.util.ModCustomAttachedData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
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
            if (client.player!= null) {
                if (client.player.isSneaking()) {
                    if (Ability1.wasPressed()) {
                        cast_ability(0, client);
                    }
                    if (Ability2.wasPressed()) {
                        cast_ability(1, client);
                    }
                    if (Ability3.wasPressed()) {
                        cast_ability(2, client);
                    }
                    if (Ability4.wasPressed()) {
                        cast_ability(3, client);
                    }
                } else {
                    if (Ability1.isPressed()) {
                        cast_ability(0, client);
                    }
                    if (Ability2.isPressed()) {
                        cast_ability(1, client);
                    }
                    if (Ability3.isPressed()) {
                        cast_ability(2, client);
                    }
                    if (Ability4.isPressed()) {
                        cast_ability(3, client);
                    }
                }
            }
        });
    }
    private static void cast_ability(Integer slot, MinecraftClient client) {
        if(client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("copy")) {
            if (client.player.isSneaking()) {
                ModCustomAttachedData data = client.player.getAttachedOrElse(ABILITIES,ModCustomAttachedData.DEFAULT);
                if (data.stringList().get(slot).contains("size")){
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(1);
                }
                client.player.setAttached(ABILITIES, data.setString(slot,"copy"));
            }else{
                if (client.player.getAttached(ABILITIES).stringList().get(slot).equals("copy")) {
                    if (client.targetedEntity instanceof PlayerEntity) {
                        ModCustomAttachedData data = client.player.getAttachedOrElse(ABILITIES, ModCustomAttachedData.DEFAULT);
                        client.player.setAttached(ABILITIES, data.setString(slot, client.targetedEntity.getAttached(ABILITIES).stringList().get(0) + "_copy"));
                    }
                } else {
                    chooseAblilty(client,slot);
                }
            }
        }else{
            chooseAblilty(client,slot);
        }
    }
    private static void chooseAblilty(MinecraftClient client,Integer slot) {
        if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("summon_minion")) {
            ClientPlayNetworking.send(new SpawnMobPayload(client.player.getBlockPos()));
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("size")) {
            ClientPlayNetworking.send(new ChangeSizePayload(client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot), slot));
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("fertilize")) {
            ClientPlayNetworking.send(new FertilizePayload(client.player.getBlockPos()));
        } else if (client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).contains("invisibility")) {
            ClientPlayNetworking.send(new InvisibilityPayload());
        } else if (client.player.getAttached(ABILITIES).stringList().get(slot).contains("warp")) {
            ClientPlayNetworking.send(new WarpPayload());
        }
    }
}
