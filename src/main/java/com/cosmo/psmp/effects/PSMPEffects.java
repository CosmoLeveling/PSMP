package com.cosmo.psmp.effects;

import com.cosmo.psmp.PSMP;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PSMPEffects extends StatusEffects {
    public static RegistryEntry<StatusEffect> TRUE_INVISIBILITY = registerStatusEffect("invisibility",
            new True_Invisibility(StatusEffectCategory.BENEFICIAL,6422641));

    public static RegistryEntry<StatusEffect> IMMOBILIZED = registerStatusEffect("immobilize",
            new ImmobilizeEffect());

    public static RegistryEntry<StatusEffect> registerStatusEffect(String name,StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(PSMP.MOD_ID, name),statusEffect);
    }

    public static void registerEffects() {
    }
}
