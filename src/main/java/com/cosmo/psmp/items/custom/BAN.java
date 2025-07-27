package com.cosmo.psmp.items.custom;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class BAN extends Item {
    public BAN(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!(user instanceof ServerPlayerEntity) || !(entity instanceof ServerPlayerEntity)) {
            return ActionResult.PASS;
        }
        ban(user,entity);
        return super.useOnEntity(stack, user, entity, hand);
    }
    private void ban(PlayerEntity user, LivingEntity entity){
        ServerPlayerEntity target = (ServerPlayerEntity) entity;
        ServerPlayerEntity source = (ServerPlayerEntity) user;
        MinecraftServer server = source.getServer();
        if (server != null) {
            GameProfile profile = target.getGameProfile();
            BannedPlayerList banList = server.getPlayerManager().getUserBanList();
            if (!banList.contains(profile)) {
                BannedPlayerEntry entry = new BannedPlayerEntry(
                        profile,
                        null,
                        source.getName().toString(),
                        null,
                        "Banned by Ban Hammer"
                );
                banList.add(entry);
                target.networkHandler.disconnect(Text.literal("You have been banned by the Ban Hammer!").formatted(Formatting.RED));
                user.sendMessage(Text.literal("Banned " + profile.getName()).formatted(Formatting.GREEN), false);
            }
        }
    }
}
