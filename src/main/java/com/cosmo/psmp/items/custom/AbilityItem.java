package com.cosmo.psmp.items.custom;

import com.cosmo.psmp.items.PSMPItems;
import com.cosmo.psmp.util.ModCustomAttachedData;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
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
        for (int i = 0; i <= 3; i++) {
            ModCustomAttachedData data = user.getAttachedOrElse(ABILITIES, ModCustomAttachedData.DEFAULT);
            if (Objects.equals(data.stringList().get(i), "None")) {
                user.setAttached(ABILITIES, data.setString(i, AbilityName));
                user.getStackInHand(hand).decrementUnlessCreative(1, user);
                return TypedActionResult.success(user.getStackInHand(hand), true);
            }
        }
        spawnItemParticles(user.getStackInHand(hand),10,user);

        return super.use(world, user, hand);
    }
    private void spawnItemParticles(ItemStack stack, int count, LivingEntity entity) {
        Random random = Random.create();
        for (int i = 0; i < count; i++) {
            Vec3d vec3d = new Vec3d((random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-entity.getPitch() * (float) (Math.PI / 180.0));
            vec3d = vec3d.rotateY(-entity.getYaw() * (float) (Math.PI / 180.0));
            double d = -random.nextFloat() * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d((random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-entity.getPitch() * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.rotateY(-entity.getYaw() * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            entity.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }
}
