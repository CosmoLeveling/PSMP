package com.cosmo.psmp.entities.client;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.client.feature.PumpkinGuyEyes;
import com.cosmo.psmp.entities.custom.MelonGuyEntity;
import com.cosmo.psmp.entities.custom.PumpkinGuyEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class PumpkinGuyRenderer extends MobEntityRenderer<PumpkinGuyEntity,PumpkinGuyModel<PumpkinGuyEntity>> {
    public PumpkinGuyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PumpkinGuyModel<>(ctx.getPart(PumpkinGuyModel.PUMPKINGUY)),0.5f);
        this.addFeature(new PumpkinGuyEyes<>(this));
    }

    @Override
    public Identifier getTexture(PumpkinGuyEntity entity) {
        if (entity.getARMOR().isOf(Items.LEATHER_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_leather.png");
        } else if (entity.getARMOR().isOf(Items.IRON_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_iron.png");
        } else if (entity.getARMOR().isOf(Items.CHAINMAIL_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_chain.png");
        } else if (entity.getARMOR().isOf(Items.GOLDEN_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_gold.png");
        } else if (entity.getARMOR().isOf(Items.DIAMOND_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_diamond.png");
        } else if (entity.getARMOR().isOf(Items.NETHERITE_HELMET)) {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy_netherite.png");
        } else {
            return Identifier.of(PSMP.MOD_ID, "textures/entity/pumpkin_guy/pumpkin_guy.png");
        }
    }

    @Override
    public void render(PumpkinGuyEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (livingEntity.isBaby()){
            matrixStack.scale(0.5f,0.5f,0.5f);
        } else {
            matrixStack.scale(1f,1f,1f);
        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
