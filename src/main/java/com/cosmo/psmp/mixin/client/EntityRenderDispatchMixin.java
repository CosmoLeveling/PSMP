package com.cosmo.psmp.mixin.client;



import com.cosmo.psmp.PSMPAttachmentTypes;
import com.cosmo.psmp.PSMPClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatchMixin {
	@Inject(method = "renderShadow", at = @At("HEAD"),cancellable = true)
	private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci){
		if(entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES)!=null) {
			if (entity != null && MinecraftClient.getInstance().player != null && entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES).uuidList().contains(entity.getUuid()))
				ci.cancel();
		}
	}
	@Inject(method = "renderHitbox", at = @At("HEAD"),cancellable = true)
	private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
		if(entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES)!=null) {
			if (entity != null && MinecraftClient.getInstance().player != null && entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES).uuidList().contains(entity.getUuid()))
				ci.cancel();
		}	}
	@Inject(method = "renderFire",at = @At("HEAD"),cancellable = true)
	private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation, CallbackInfo ci) {
		if(entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES)!=null) {
			if (entity != null && MinecraftClient.getInstance().player != null && entity.getWorld().getAttached(PSMPAttachmentTypes.ENTITIES).uuidList().contains(entity.getUuid()))
				ci.cancel();
		}	}
}
