package com.cosmo.psmp.items.custom;

import com.cosmo.psmp.PSMP;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class TestItem extends Item {
    public static final Identifier IMPERSONATION_KEY = Identifier.of(PSMP.MOD_ID, "impersonitem");
    public TestItem(Settings settings) {
        super(settings);
    }

}
