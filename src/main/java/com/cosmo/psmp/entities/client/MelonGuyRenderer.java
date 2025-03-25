package com.cosmo.psmp.entities.client;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.client.feature.MelonGuyEyes;
import com.cosmo.psmp.entities.custom.MelonGuyEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class MelonGuyRenderer extends MobEntityRenderer<MelonGuyEntity,MelonGuyModel<MelonGuyEntity>> {
    public MelonGuyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MelonGuyModel<>(ctx.getPart(MelonGuyModel.MELONGUY)),0.5f);
        this.addFeature(new MelonGuyEyes<>(this));
    }

    @Override
    public Identifier getTexture(MelonGuyEntity entity) {
        if (entity.getInventory().getStack(10).isOf(Items.LEATHER_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_leather.png");
        } else if (entity.getInventory().getStack(10).isOf(Items.IRON_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_iron.png");
        } else if (entity.getInventory().getStack(10).isOf(Items.CHAINMAIL_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_chain.png");
        } else if (entity.getInventory().getStack(10).isOf(Items.GOLDEN_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_gold.png");
        } else if (entity.getInventory().getStack(10).isOf(Items.DIAMOND_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_diamond.png");
        } else if (entity.getInventory().getStack(10).isOf(Items.NETHERITE_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy_netherite.png");
        } else {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy.png");
        }
    }

    @Override
    public void render(MelonGuyEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (livingEntity.isBaby()){
            matrixStack.scale(0.5f,0.5f,0.5f);
        } else {
            matrixStack.scale(1f,1f,1f);
        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
