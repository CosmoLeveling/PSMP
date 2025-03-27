package com.cosmo.psmp;

import com.cosmo.psmp.blocks.PSMPBlocks;
import com.cosmo.psmp.commands.PSMPCommands;
import com.cosmo.psmp.effects.PSMPEffects;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.custom.MinionEntity;
import com.cosmo.psmp.items.PSMPItems;
import com.cosmo.psmp.networking.*;
import com.cosmo.psmp.screen.PSMPScreenHandlers;
import com.cosmo.psmp.util.Abilities;
import com.cosmo.psmp.util.PSMPLootTableModifiers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PSMP implements ModInitializer {
	public static final String MOD_ID = "psmp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Power SMP Core Mod");

		//Networking
		PayloadTypeRegistry.playC2S().register(SpawnMobPayload.ID, SpawnMobPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ChangeSizePayload.ID, ChangeSizePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(FertilizePayload.ID, FertilizePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PhasePayload.ID, PhasePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(WarpPayload.ID, WarpPayload.CODEC);

		//Entity Attributes
		FabricDefaultAttributeRegistry.register(PSMPEntities.PUMPKIN_GUY, MinionEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(PSMPEntities.MELON_GUY, MinionEntity.createAttributes());

		//register classes
		PSMPLootTableModifiers.modifyLootTables();
		PSMPAttachmentTypes.init();
		PSMPCommands.register();
		Abilities.register();
		PSMPItems.registerItems();
		PSMPScreenHandlers.init();
		PSMPEntities.registerEntities();
		PSMPEffects.registerEffects();
		PSMPBlocks.init();
	}
}