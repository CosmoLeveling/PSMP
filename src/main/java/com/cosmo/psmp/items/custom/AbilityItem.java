package com.cosmo.psmp.items.custom;

import com.cosmo.psmp.util.ModCustomAttachedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;

import static com.cosmo.psmp.PSMPAttachmentTypes.ABILITIES;

public class AbilityItem extends Item {
    private String AbilityName;
    public AbilityItem(Settings settings, String ability_name) {
        super(settings);
        AbilityName=ability_name;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient){
        for (int i = 0; i <= 3; i++){
            if (Objects.equals(user.getAttached(ABILITIES).stringList().get(i), "None")) {
                ModCustomAttachedData data = user.getAttachedOrElse(ABILITIES, ModCustomAttachedData.DEFAULT);
                user.setAttached(ABILITIES, data.setString(i, AbilityName));
                user.getStackInHand(hand).decrementUnlessCreative(1,user);
                return TypedActionResult.success(user.getStackInHand(hand),true);
            }
        }
        }
        return super.use(world, user, hand);
    }
}
