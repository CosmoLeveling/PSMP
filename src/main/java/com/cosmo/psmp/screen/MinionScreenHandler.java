package com.cosmo.psmp.screen;

import com.cosmo.psmp.entities.custom.MinionEntity;
import com.cosmo.psmp.networking.IntPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MinionScreenHandler extends ScreenHandler {
    private final MinionEntity entity;
    private final SimpleInventory inventory;

    //Client Constructor
    public MinionScreenHandler(int syncId, PlayerInventory playerInventory, IntPayload payload)
    {
        this(syncId,playerInventory, (MinionEntity) playerInventory.player.getWorld().getEntityById(payload.id()));
    }


    //Main Constructor - (Runs on server)
    public MinionScreenHandler(int syncId, PlayerInventory playerInventory, MinionEntity entity) {
        super(PSMPScreenHandlers.MINION_SCREEN_HANDLER, syncId);
        this.entity = entity;
        this.inventory = entity.getInventory();
        addMinionInvo();
        addPlayerInvo(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addMinionInvo() {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(this.inventory,column,8+(column*18),51));
        }
        addSlot(new Slot(this.inventory,9,26,26));
        addSlot(new Slot(this.inventory,10,62,26));
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory,column,8+(column*18),142));
        }
    }

    private void addPlayerInvo(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory,9+(column+(row*9)),8+(column*18),84+(row*18)));
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    public MinionEntity getEntity() {
        return this.entity;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
