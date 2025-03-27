package com.cosmo.psmp.entities.client.feature;

import com.cosmo.psmp.entities.client.PumpkinGuyModel;
import com.cosmo.psmp.entities.custom.MinionEntity;
import com.google.common.collect.Maps;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.Map;

public class PumpkinArmorFeature<T extends MinionEntity, M extends PumpkinGuyModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();
    private final M ArmorModel;
    private final SpriteAtlasTexture armorTrimsAtlas;
    public PumpkinArmorFeature(FeatureRendererContext<T, M> context,M model, BakedModelManager bakery) {
        super(context);
        this.ArmorModel = model;
        this.armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        this.renderArmor(matrices, vertexConsumers, entity, EquipmentSlot.HEAD, light,ArmorModel);
    }
    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, M armorModel) {
        ItemStack itemStack = entity.getEquippedStack(armorSlot);
        if (itemStack.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getSlotType() == armorSlot) {
                this.getContextModel().copyPumpkinStateTo(armorModel);
                this.setVisible(armorModel,EquipmentSlot.HEAD);
                ArmorMaterial armorMaterial = armorItem.getMaterial().value();
                int i = itemStack.isIn(ItemTags.DYEABLE) ? ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(itemStack, -6265536)) : -1;

                for (ArmorMaterial.Layer layer : armorMaterial.layers()) {
                    int j = layer.isDyeable() ? i : -1;
                    this.renderArmorParts(matrices, vertexConsumers, light,armorModel, j, layer.getTexture(false));
                }

                ArmorTrim armorTrim = itemStack.get(DataComponentTypes.TRIM);
                if (armorTrim != null) {
                    this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, armorTrim,armorModel,false);
                }

                if (itemStack.hasGlint()) {
                    this.renderGlint(matrices, vertexConsumers, light,armorModel);
                }
            }
        }
    }
    private void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,M armorModel) {
        armorModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light, OverlayTexture.DEFAULT_UV);
    }
    protected void setVisible(M model, EquipmentSlot slot) {
        model.setVisible(false);
    }
    private void renderTrim(
            RegistryEntry<ArmorMaterial> armorMaterial,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            ArmorTrim trim,
            M armorModel,
            boolean leggings
    ) {
        Sprite sprite = this.armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(armorMaterial) : trim.getGenericModelId(armorMaterial));
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal()))
        );
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }
    private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, M armorModel, int i, Identifier identifier) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(identifier));
        armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, i);
    }
}
