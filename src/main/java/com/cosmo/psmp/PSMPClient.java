package com.cosmo.psmp;

import com.cosmo.psmp.client.AbilityHudOverlay;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.client.PumpkinGuyModel;
import com.cosmo.psmp.entities.client.PumpkinGuyRenderer;
import com.cosmo.psmp.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PSMPClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyInputHandler.registerKeyInputs();
        EntityModelLayerRegistry.registerModelLayer(PumpkinGuyModel.PUMPKINGUY,PumpkinGuyModel::getTexturedModelData);
        EntityRendererRegistry.register(PSMPEntities.PUMPKIN_GUY, PumpkinGuyRenderer::new);
        HudRenderCallback.EVENT.register(new AbilityHudOverlay());

    }
}
