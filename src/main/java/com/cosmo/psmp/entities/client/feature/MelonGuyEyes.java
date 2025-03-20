package com.cosmo.psmp.entities.client.feature;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.client.MelonGuyModel;
import com.cosmo.psmp.entities.client.PumpkinGuyModel;
import com.cosmo.psmp.entities.custom.MelonGuyEntity;
import com.cosmo.psmp.entities.custom.PumpkinGuyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MelonGuyEyes<T extends MelonGuyEntity, M extends MelonGuyModel<T>> extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(PSMP.MOD_ID,"textures/entity/melon_guy/melon_guy_glow.png"));

    public MelonGuyEyes(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}