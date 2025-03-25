package com.cosmo.psmp.util;

import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.inventory.Inventory;

public interface ServerPlayerEntityAccess {
    void access(MinionEntity minionEntity, Inventory inventory);
}
