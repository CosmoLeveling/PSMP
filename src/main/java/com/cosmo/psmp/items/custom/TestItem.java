package com.cosmo.psmp.items.custom;

import com.cosmo.psmp.PSMP;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.ladysnake.impersonate.Impersonator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TestItem extends Item {
    public static final Identifier IMPERSONATION_KEY = Identifier.of(PSMP.MOD_ID, "impersonitem");
    public TestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user instanceof ServerPlayerEntity serverPlayerEntity) {
            startImpersonation(getNames(serverPlayerEntity), user.getServer().getPlayerManager().getPlayerList(), IMPERSONATION_KEY);
        }

        return super.use(world, user, hand);
    }

    private static int startImpersonation( Collection<GameProfile> profiles, Collection<ServerPlayerEntity> players, Identifier impersonationKey) {
        assert !profiles.isEmpty();
        Iterator<GameProfile> it = profiles.iterator();
        GameProfile disguise = it.next();
        int count = 0;
        stopImpersonation(players, impersonationKey);
        for (ServerPlayerEntity player : players) {
            Impersonator.get(player).impersonate(impersonationKey, disguise);
            ++count;
        }
        return count;
    }

    private static int stopImpersonation(Collection<ServerPlayerEntity> players, Identifier key) {
        int count = 0;
        for (ServerPlayerEntity player : players) {
            Impersonator impersonator = Impersonator.get(player);
            GameProfile impersonated;
            if (key == null) {
                impersonated = impersonator.getImpersonatedProfile();
                impersonator.stopImpersonations();
            } else {
                impersonated = impersonator.stopImpersonation(key);
            }
            if (impersonated != null) {
                ++count;
            }
        }
        return count;
    }
    public Collection<GameProfile> getNames(ServerPlayerEntity user) {
        List<ServerPlayerEntity> list = List.of(user.getServer().getPlayerManager().getPlayer("shokatunes"));
        if (!list.isEmpty()) {
            List<GameProfile> list2 = Lists.newArrayList();

            for (ServerPlayerEntity serverPlayerEntity : list) {
                list2.add(serverPlayerEntity.getGameProfile());
            }

            return list2;
        }
        return null;
    }
}
