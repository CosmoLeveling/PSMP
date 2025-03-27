package com.cosmo.psmp.entities.client;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.client.feature.MelonArmorFeature;
import com.cosmo.psmp.entities.client.feature.MelonGuyEyes;
import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MelonGuyRenderer extends MobEntityRenderer<MinionEntity,MelonGuyModel<MinionEntity>> {
    public MelonGuyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MelonGuyModel<>(ctx.getPart(MelonGuyModel.MELONGUY)),0.5f);
        this.addFeature(new MelonGuyEyes<>(this));
        this.addFeature(new MelonArmorFeature<>(this,new MelonGuyModel<>(ctx.getPart(MelonGuyModel.MELONGUY_ARMOR)),ctx.getModelManager()));
    }

    @Override
    public Identifier getTexture(MinionEntity entity) {
        return Identifier.of(PSMP.MOD_ID, "textures/entity/melon_guy/melon_guy.png");
    }

    @Override
    public void render(MinionEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (livingEntity.isBaby()){
            matrixStack.scale(0.5f,0.5f,0.5f);
        } else {
            matrixStack.scale(1f,1f,1f);
        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
