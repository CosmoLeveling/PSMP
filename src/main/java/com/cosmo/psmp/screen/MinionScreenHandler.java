package com.cosmo.psmp.screen;

import com.cosmo.psmp.entities.custom.MinionEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MinionScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final MinionEntity entity;

    public MinionScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, MinionEntity entity, int slotColumnCount) {
        super(null, syncId);
        this.inventory = inventory;
        this.entity = entity;
        int i = 3;
        inventory.onOpen(playerInventory.player);
        int j = -18;
        if (slotColumnCount > 0) {
            for(int k = 0; k < 3; ++k) {
                for(int l = 0; l < slotColumnCount; ++l) {
                    this.addSlot(new Slot(inventory, 1 + l + k * slotColumnCount, 80 + l * 18, 18 + k * 18));
                }
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 102 + k * 18 + -18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            int i = this.inventory.size() + 1;
            if (slot < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 1 || !this.insertItem(itemStack2, 2, i, false)) {
                int k = i + 27;
                int m = k + 9;
                if (slot >= k && slot < m) {
                    if (!this.insertItem(itemStack2, i, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= i && slot < k) {
                    if (!this.insertItem(itemStack2, k, m, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, k, k, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !this.entity.areInventoriesDifferent(this.inventory) && this.inventory.canPlayerUse(player) && this.entity.isAlive() && player.canInteractWithEntity(this.entity, (double)4.0F);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
