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

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;

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
        if(!client.options.hudHidden) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, Gui);
            drawContext.drawTexture(Gui, 0, y - 48, 0, 0, 48, 48,
                    48, 48);
            ModCustomAttachedData data = client.player.getAttachedOrElse(ABILITIES, ModCustomAttachedData.DEFAULT);
            client.player.setAttached(ABILITIES, data);
            if (!Objects.equals(client.player.getAttached(ABILITIES).stringList().get(0), "None") && Objects.equals(client.player.getAttached(ABILITIES).Cooldowns().get(0),0)) {
                draw_Ability(0, client, drawContext, 6, y - 42);
            }
            if (!Objects.equals(client.player.getAttached(ABILITIES).stringList().get(1), "None") && Objects.equals(client.player.getAttached(ABILITIES).Cooldowns().get(1),0)) {
                draw_Ability(1, client, drawContext, 26, y - 42);
            }
            if (!Objects.equals(client.player.getAttached(ABILITIES).stringList().get(2), "None") && Objects.equals(client.player.getAttached(ABILITIES).Cooldowns().get(2),0)) {
                draw_Ability(2, client, drawContext, 6, y - 22);
            }
            if (!Objects.equals(client.player.getAttached(ABILITIES).stringList().get(3), "None") && Objects.equals(client.player.getAttached(ABILITIES).Cooldowns().get(3),0)) {
                draw_Ability(3, client, drawContext, 26, y - 22);
            }
        }
    }
    private void draw_Ability(int slot, MinecraftClient client, DrawContext drawContext, int x,int y) {
        if(client.player.getAttached(ABILITIES).stringList().get(slot).contains("copy")){
            if(Objects.equals(client.player.getAttached(ABILITIES).stringList().get(slot), "copy")) {
                drawContext.drawTexture(Identifier.of(PSMP.MOD_ID, "textures/item/abilities/" + client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot) + ".png"),  x, y, 0, 0, 16, 16,
                        16, 16);
            }else{
                drawContext.drawTexture(Identifier.of(PSMP.MOD_ID, "textures/item/abilities/" + client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot).replace("_copy","") + ".png"),  x, y, 0, 0, 16, 16,
                        16, 16);
                drawContext.drawTexture(Identifier.of(PSMP.MOD_ID, "textures/gui/abilities/copy_outline.png"), x, y, 0, 0, 16, 16,
                        16, 16);
            }
        }else {
            drawContext.drawTexture(Identifier.of(PSMP.MOD_ID, "textures/item/abilities/" + client.player.getAttached(PSMPAttachmentTypes.ABILITIES).stringList().get(slot) + ".png"),  x, y, 0, 0, 16, 16,
                    16, 16);
        }
    }
}
