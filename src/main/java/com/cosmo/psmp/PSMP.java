package com.cosmo.psmp;

import com.cosmo.psmp.blocks.PSMPBlocks;
import com.cosmo.psmp.commands.PSMPCommands;
import com.cosmo.psmp.effects.PSMPEffects;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.custom.MinionEntity;
import com.cosmo.psmp.items.PSMPItemGroups;
import com.cosmo.psmp.items.PSMPItems;
import com.cosmo.psmp.networking.*;
import com.cosmo.psmp.screen.PSMPScreenHandlers;
import com.cosmo.psmp.util.Abilities;
import com.cosmo.psmp.util.PSMPLootTableModifiers;
import com.cosmo.psmp.world.features.PocketDimOutPortal;
import com.cosmo.psmp.world.gen.PSMPWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.logging.Logger;

public class PSMP implements ModInitializer {
	public static final String MOD_ID = "psmp";
	public static final Logger LOGGER = Logger.getLogger(MOD_ID);
	public static final TrackedDataHandler<MinionEntity.State> MINION_STATE = TrackedDataHandler.create(MinionEntity.State.PACKET_CODEC);
	public static final Feature<DefaultFeatureConfig> PocketDimOutPortal = Registry.register(Registries.FEATURE, Identifier.of(PSMP.MOD_ID,"pocket_dim_out_portal"),new PocketDimOutPortal(DefaultFeatureConfig.CODEC));

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Power SMP Core Mod");
		AttackEntityCallback.EVENT.register((PlayerEntity player, World world, net.minecraft.util.Hand hand, Entity target, EntityHitResult hitResult) -> {
			StatusEffectInstance effect = player.getStatusEffect(PSMPEffects.IMMOBILIZED);
			if (effect != null && effect.getAmplifier() >= 0) {
				return ActionResult.FAIL; // Cancel attack
			}
			return ActionResult.PASS; // Allow normal behavior
		});
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((LivingEntity entity, DamageSource source, float amount) -> {
			if (entity instanceof PlayerEntity player) {
				StatusEffectInstance effect = player.getStatusEffect(PSMPEffects.IMMOBILIZED);
                return effect == null || effect.getAmplifier() < 0; // Cancel the damage
			}
			return true; // Allow normal damage
		});
		//Networking
		PayloadTypeRegistry.playC2S().register(SpawnMobPayload.ID, SpawnMobPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ChangeSizePayload.ID, ChangeSizePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(FertilizePayload.ID, FertilizePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(InvisibilityPayload.ID, InvisibilityPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(WarpPayload.ID, WarpPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PocketDimPayload.ID, PocketDimPayload.CODEC);

		//Entity Attributes
		FabricDefaultAttributeRegistry.register(PSMPEntities.PUMPKIN_GUY, MinionEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(PSMPEntities.MELON_GUY, MinionEntity.createAttributes());

		//register classes
		TrackedDataHandlerRegistry.register(MINION_STATE);
		PSMPLootTableModifiers.modifyLootTables();
		PSMPAttachmentTypes.init();
		PSMPCommands.register();
		Abilities.register();
		PSMPItems.registerItems();
		PSMPScreenHandlers.init();
		PSMPItemGroups.init();
		PSMPEntities.registerEntities();
		PSMPEffects.registerEffects();
		PSMPBlocks.init();
		PSMPWorldGeneration.generateWorldGen();
		LOGGER.info("Power SMP Core Mod Initialized");
	}
}