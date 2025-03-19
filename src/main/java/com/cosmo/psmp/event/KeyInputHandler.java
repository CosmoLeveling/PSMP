package com.cosmo.psmp.event;

import com.cosmo.psmp.PSMPAttachmentTypes;
import com.cosmo.psmp.networking.ChangeSizePayload;
import com.cosmo.psmp.networking.SpawnMobPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;


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
            while (Ability1.wasPressed()) {
                cast_ability(0,client);
            }
            while (Ability2.wasPressed()) {
                cast_ability(1,client);
            }
            while (Ability3.wasPressed()) {
                cast_ability(2,client);
            }
            while (Ability4.wasPressed()) {
                cast_ability(3,client);
            }
        });
    }
    private static void cast_ability(Integer slot, MinecraftClient client) {
        if(Objects.equals(client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(slot), "summon_minion")){
            ClientPlayNetworking.send(new SpawnMobPayload(client.player.getBlockPos()));
        }else if(Objects.equals(client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(slot), "grow_size")||Objects.equals(client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(slot), "reset_size")||Objects.equals(client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(slot), "shrink_size")){
            ClientPlayNetworking.send(new ChangeSizePayload(client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(slot),slot));
        }
    }
}
