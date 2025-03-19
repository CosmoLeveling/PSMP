package com.cosmo.psmp;

import com.cosmo.psmp.commands.arguments.AbilityArgumentType;
import com.cosmo.psmp.entities.PSMPEntities;
import com.cosmo.psmp.entities.custom.PumpkinGuyEntity;
import com.cosmo.psmp.items.PSMPItems;
import com.cosmo.psmp.networking.SpawnMobPayload;
import com.cosmo.psmp.networking.ChangeSizePayload;
import com.cosmo.psmp.util.ModCustomAttachedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE;
import static net.minecraft.text.Text.literal;

public class PSMP implements ModInitializer {
	public static final String MOD_ID = "psmp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Power SMP Core Mod");
		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID,"abilitytype"),AbilityArgumentType.class, ConstantArgumentSerializer.of(AbilityArgumentType::new));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("ability")
				.then(CommandManager.argument("slot",IntegerArgumentType.integer(0,3))
				.then(CommandManager.argument("ability", AbilityArgumentType.ability())
				.executes(context -> {
					ModCustomAttachedData data = context.getSource().getPlayer().getAttachedOrElse(YOUR_ATTACHMENT_TYPE,ModCustomAttachedData.DEFAULT);
					context.getSource().getPlayer().setAttached(YOUR_ATTACHMENT_TYPE, data.setString(IntegerArgumentType.getInteger(context,"slot"),AbilityArgumentType.getAbility(context,"ability").asString()));
					context.getSource().sendFeedback(() -> Text.literal("Set Slot %s to %s".formatted(IntegerArgumentType.getInteger(context,"slot"),AbilityArgumentType.getAbility(context,"ability").asString())),false);
					return 1;
				})))));
		PayloadTypeRegistry.playC2S().register(SpawnMobPayload.ID, SpawnMobPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ChangeSizePayload.ID, ChangeSizePayload.CODEC);
		PSMPAttachmentTypes.init();
		ServerPlayNetworking.registerGlobalReceiver(ChangeSizePayload.ID, ((changesizePayload, context) -> {
			context.server().execute(() -> {
				if (context.player().isSneaking()) {
					if (Objects.equals(changesizePayload.mode(), "reset_size")) {
						ModCustomAttachedData data = context.player().getAttachedOrElse(YOUR_ATTACHMENT_TYPE,ModCustomAttachedData.DEFAULT);
						context.player().setAttached(YOUR_ATTACHMENT_TYPE, data.setString(changesizePayload.slot(),"grow_size"));
					} else if (Objects.equals(changesizePayload.mode(), "grow_size")) {
						ModCustomAttachedData data = context.player().getAttachedOrElse(YOUR_ATTACHMENT_TYPE,ModCustomAttachedData.DEFAULT);
						context.player().setAttached(YOUR_ATTACHMENT_TYPE, data.setString(changesizePayload.slot(),"shrink_size"));
					} else {
						ModCustomAttachedData data = context.player().getAttachedOrElse(YOUR_ATTACHMENT_TYPE,ModCustomAttachedData.DEFAULT);
						context.player().setAttached(YOUR_ATTACHMENT_TYPE, data.setString(changesizePayload.slot(),"reset_size"));
					}
				} else {
					if (Objects.equals(changesizePayload.mode(), "reset_size")) {
						context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(1);
					} else if (Objects.equals(changesizePayload.mode(), "grow_size")) {
						double max_height=16;
						for (int I = context.player().getBlockY();I<=context.player().getBlockY()+32;I++){
							if(context.player().getWorld().getBlockState(context.player().getBlockPos().withY(I)).isAir()) {
								max_height = ((double) (I - context.player().getBlockY()) / 2)+0.6;
							}else{
								break;
							}
						}
						context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() + 0.01, 0.45, max_height));
					} else {
						context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).setBaseValue(Math.clamp(context.player().getAttributeInstance(EntityAttributes.GENERIC_SCALE).getBaseValue() - 0.01,0.45,16));
					}
				}
			});
		}));
		ServerPlayNetworking.registerGlobalReceiver(SpawnMobPayload.ID, ((spawnMobPayload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();
				if (Objects.equals(player.getMainHandStack().getItem(),Items.PUMPKIN)){
					PSMPEntities.PUMPKIN_GUY.spawn((ServerWorld) context.player().getWorld(), context.player().getBlockPos(),
							SpawnReason.TRIGGERED).setOwner(player);
					player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
				}
			});
		}));
		FabricDefaultAttributeRegistry.register(PSMPEntities.PUMPKIN_GUY, PumpkinGuyEntity.createAttributes());
		PSMPItems.registerItems();
		PSMPEntities.registerEntities();
	}
}