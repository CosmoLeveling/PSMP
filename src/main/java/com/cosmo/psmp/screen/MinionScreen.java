package com.cosmo.psmp.screen;

import com.cosmo.psmp.PSMP;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MinionScreen extends HandledScreen<MinionScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(PSMP.MOD_ID,"textures/gui/entity/minion_screen.png");

    public MinionScreen(MinionScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE,this.x,this.y,0,0,this.backgroundWidth,this.backgroundHeight);
    }
}
