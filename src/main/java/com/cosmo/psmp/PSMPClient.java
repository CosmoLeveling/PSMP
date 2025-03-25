package com.cosmo.psmp;

import com.cosmo.psmp.client.AbilityHudOverlay;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.client.MelonGuyModel;
import com.cosmo.psmp.entities.client.MelonGuyRenderer;
import com.cosmo.psmp.entities.client.PumpkinGuyModel;
import com.cosmo.psmp.entities.client.PumpkinGuyRenderer;
import com.cosmo.psmp.event.KeyInputHandler;
import com.cosmo.psmp.screen.MinionScreen;
import com.cosmo.psmp.screen.PSMPScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class PSMPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyInputHandler.registerKeyInputs();
        EntityModelLayerRegistry.registerModelLayer(PumpkinGuyModel.PUMPKINGUY,PumpkinGuyModel::getTexturedModelData);
        EntityRendererRegistry.register(PSMPEntities.PUMPKIN_GUY, PumpkinGuyRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MelonGuyModel.MELONGUY,MelonGuyModel::getTexturedModelData);
        EntityRendererRegistry.register(PSMPEntities.MELON_GUY, MelonGuyRenderer::new);
        HandledScreens.register(PSMPScreenHandlers.MINION_SCREEN_HANDLER, MinionScreen::new);
        HudRenderCallback.EVENT.register(new AbilityHudOverlay());
        //S2C

    }
}
