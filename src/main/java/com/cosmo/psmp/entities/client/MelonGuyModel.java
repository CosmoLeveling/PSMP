package com.cosmo.psmp.entities.client;

import com.cosmo.psmp.PSMP;
import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class MelonGuyModel<T extends MinionEntity> extends SinglePartEntityModel<T> {
	public static final EntityModelLayer MELONGUY = new EntityModelLayer(Identifier.of(PSMP.MOD_ID,"melon_guy"),"main");
	public static final EntityModelLayer MELONGUY_ARMOR = new EntityModelLayer(Identifier.of(PSMP.MOD_ID,"melon_guy"),"armor");
	private final ModelPart Main;
	private final ModelPart Body;
	private final ModelPart InnerBody;
	private final ModelPart LegL;
	private final ModelPart LegR;
	private final ModelPart ArmL;
	private final ModelPart Sword;
	private final ModelPart ArmR;
	private final ModelPart Stem;
	private final ModelPart BackPack;
	public MelonGuyModel(ModelPart root) {
		this.Main = root.getChild("Main");
		this.Body = this.Main.getChild("Body");
		this.InnerBody = this.Body.getChild("InnerBody");
		this.LegL = this.Body.getChild("LegL");
		this.LegR = this.Body.getChild("LegR");
		this.ArmL = this.Body.getChild("ArmL");
		this.Sword = this.ArmL.getChild("Sword");
		this.ArmR = this.Body.getChild("ArmR");
		this.Stem = this.Body.getChild("Stem");
		this.BackPack = this.Body.getChild("BackPack");
	}
	public void setVisible(boolean visible) {
		this.InnerBody.visible = visible;
		this.LegL.visible = visible;
		this.LegR.visible = visible;
		this.ArmL.visible = visible;
		this.ArmR.visible = visible;
		this.Stem.visible = visible;
		this.BackPack.visible = visible;
	}
	public void copyMelonStateTo(MelonGuyModel<T> model) {
		model.BackPack.copyTransform(this.BackPack);
		model.Stem.copyTransform(this.Stem);
		model.ArmR.copyTransform(this.ArmR);
		model.ArmL.copyTransform(this.ArmL);
		model.LegR.copyTransform(this.LegR);
		model.LegL.copyTransform(this.LegL);
		model.Sword.copyTransform(this.Sword);
		model.Body.copyTransform(this.Body);
		model.InnerBody.copyTransform(this.InnerBody);
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData Main = modelPartData.addChild("Main", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 22.0F, 0.775F));

		ModelPartData Body = Main.addChild("Body", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -9.0F, -4.775F, 8.0F, 8.0F, 8.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData InnerBody = Body.addChild("InnerBody", ModelPartBuilder.create().uv(32, 0).cuboid(-11.0F, -7.0F, -4.775F, 8.0F, 7.0F, 8.0F, new Dilation(0.0F))
				.uv(40, 20).cuboid(-10.0F, -6.0F, -5.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(46, 23).cuboid(-6.0F, -6.0F, -5.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData LegL = Body.addChild("LegL", ModelPartBuilder.create().uv(48, 19).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 0.0F, -0.775F));

		ModelPartData LegR = Body.addChild("LegR", ModelPartBuilder.create().uv(48, 15).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-9.0F, 0.0F, -0.775F));

		ModelPartData ArmL = Body.addChild("ArmL", ModelPartBuilder.create(), ModelTransform.pivot(-3.9247F, -3.9123F, -0.775F));

		ModelPartData cube_r1 = ArmL.addChild("cube_r1", ModelPartBuilder.create().uv(32, 15).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

		ModelPartData Sword = ArmL.addChild("Sword", ModelPartBuilder.create().uv(32, 24).cuboid(0.0F, -1.0F, -5.25F, 0.0F, 2.0F, 6.0F, new Dilation(0.0F))
				.uv(55, 27).cuboid(-0.5F, -1.0F, 0.75F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.9247F, 1.6623F, -2.25F, 0.0F, 0.0F, -3.1416F));

		ModelPartData ArmR = Body.addChild("ArmR", ModelPartBuilder.create(), ModelTransform.pivot(-10.1866F, -4.1059F, -0.775F));

		ModelPartData cube_r2 = ArmR.addChild("cube_r2", ModelPartBuilder.create().uv(39, 15).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7418F));

		ModelPartData Stem = Body.addChild("Stem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r3 = Stem.addChild("cube_r3", ModelPartBuilder.create().uv(32, 20).cuboid(-4.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -6.675F, -0.775F, 0.3054F, 0.0F, 0.0F));

		ModelPartData BackPack = Body.addChild("BackPack", ModelPartBuilder.create().uv(1, 20).cuboid(-4.0F, -5.0F, -1.0F, 8.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.0F, -1.0F, 4.225F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.animateMovement(PumpkinGuyAnimations.Walk, limbAngle,limbDistance,2f,2.5f);
		this.updateAnimation(entity.idleAnimationState, PumpkinGuyAnimations.Idle,animationProgress,1f);
		if (entity.getState().equals(MinionEntity.State.SITTING)){
			animate(PumpkinGuyAnimations.Sitting);
		}
	}

	@Override
	public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
		this.Sword.visible = entity.getTool().isOf(Items.IRON_SWORD);
		this.BackPack.visible = entity.hasBackpack();
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		Main.render(matrices, vertexConsumer, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return Main;
	}
}