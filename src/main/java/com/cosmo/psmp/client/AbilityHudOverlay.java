package com.cosmo.psmp.client;

import com.cosmo.psmp.util.ModCustomAttachedData;
import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.PSMPAttachmentTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE;

public class AbilityHudOverlay implements HudRenderCallback {
    private static final Identifier Gui = Identifier.of(PSMP.MOD_ID,
            "textures/gui/abilities/ability_gui.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        int x = 0;
        int y = 0;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client!=null) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            x = width / 2;
            y = height;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0, Gui);
        drawContext.drawTexture(Gui,0,y-48,0,0,48,48,
                48,48);
        ModCustomAttachedData data = client.player.getAttachedOrElse(YOUR_ATTACHMENT_TYPE,ModCustomAttachedData.DEFAULT);
        client.player.setAttached(YOUR_ATTACHMENT_TYPE, data);
        if (!Objects.equals(client.player.getAttached(YOUR_ATTACHMENT_TYPE).stringList().get(0), "None")) {
            drawContext.drawTexture(Identifier.of(PSMP.MOD_ID,"textures/gui/abilities/"+client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(0)+".png"),6,y-42,0,0,16,16,
                    16,16);
        }
        if (!Objects.equals(client.player.getAttached(YOUR_ATTACHMENT_TYPE).stringList().get(1), "None")) {
            drawContext.drawTexture(Identifier.of(PSMP.MOD_ID,"textures/gui/abilities/"+client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(1)+".png"),26,y-42,0,0,16,16,
                    16,16);
        }
        if (!Objects.equals(client.player.getAttached(YOUR_ATTACHMENT_TYPE).stringList().get(2), "None")) {
            drawContext.drawTexture(Identifier.of(PSMP.MOD_ID,"textures/gui/abilities/"+client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(2)+".png"),6,y-22,0,0,16,16,
                    16,16);
        }
        if (!Objects.equals(client.player.getAttached(YOUR_ATTACHMENT_TYPE).stringList().get(3), "None")) {
            drawContext.drawTexture(Identifier.of(PSMP.MOD_ID,"textures/gui/abilities/"+client.player.getAttached(PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE).stringList().get(3)+".png"),26,y-22,0,0,16,16,
                    16,16);
        }
    }
}
