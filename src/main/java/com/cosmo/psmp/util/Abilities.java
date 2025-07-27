package com.cosmo.psmp.util;

import com.cosmo.psmp.PSMPAttachmentTypes;
import com.cosmo.psmp.networking.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class Abilities {
    public static void register() {
        //Change Size
        ServerPlayNetworking.registerGlobalReceiver(ChangeSizePayload.ID, ((changesizePayload, context) ->
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                ChangeSizePayload.handle(player,changesizePayload);
            })));

        //Summon Minion
        ServerPlayNetworking.registerGlobalReceiver(SpawnMobPayload.ID, ((spawnMobPayload, context) ->
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                SpawnMobPayload.handle(player);
            })));

        //Invisibility
        ServerPlayNetworking.registerGlobalReceiver(InvisibilityPayload.ID,(((invisibilityPayload, context) -> {
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                InvisibilityPayload.handle(player);
             });
        })));

        //Fertilize
        ServerPlayNetworking.registerGlobalReceiver(FertilizePayload.ID, (((fertilizePayload, context) ->
            context.server().execute(() -> {
                PlayerEntity player = context.player();
                FertilizePayload.handle(player);
            }))));
        //Warp
        ServerPlayNetworking.registerGlobalReceiver(WarpPayload.ID,(((warpPayload, context) ->
                context.server().execute(() -> {
                    PlayerEntity player = context.player();
                    WarpPayload.handle(player,warpPayload);
                }))));
        //Pocket Dim
        ServerPlayNetworking.registerGlobalReceiver(PocketDimPayload.ID,(((pocketDimPayload, context) ->
                context.server().execute(() -> {
                    PlayerEntity player = context.player();
                    PocketDimPayload.handle(player);
                }))));
    }
}
