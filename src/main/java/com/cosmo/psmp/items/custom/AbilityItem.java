package com.cosmo.psmp.items.custom;

import com.cosmo.psmp.commands.arguments.AbilityArgumentType;
import com.cosmo.psmp.util.ModCustomAttachedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.YOUR_ATTACHMENT_TYPE;

public class AbilityItem extends Item {
    private String AbilityName;
    public AbilityItem(Settings settings, String ability_name) {
        super(settings);
        AbilityName=ability_name;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        for (int i = 0; i <= 3; i++){
            if (Objects.equals(user.getAttached(YOUR_ATTACHMENT_TYPE).stringList().get(i), "None")) {
                ModCustomAttachedData data = user.getAttachedOrElse(YOUR_ATTACHMENT_TYPE, ModCustomAttachedData.DEFAULT);
                user.setAttached(YOUR_ATTACHMENT_TYPE, data.setString(i, AbilityName));
                user.getStackInHand(hand).decrementUnlessCreative(1,user);
            }
            return TypedActionResult.success(user.getStackInHand(hand),true);
        }
        return super.use(world, user, hand);
    }
}
