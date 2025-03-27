package com.cosmo.psmp.screen;

import com.cosmo.psmp.entities.custom.MinionEntity;
import com.cosmo.psmp.networking.IntPayload;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MinionScreenHandler extends ScreenHandler {
    private final MinionEntity entity;
    private final SimpleInventory inventory;
    private Boolean backpack = false;
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
        if (entity.hasBackpack()) {
            backpack = true;
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(this.inventory, column, 8 + (column * 18), 51));
            }
        }
        addSlot(new Slot(this.inventory,9,8,26){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.IRON_SWORD)||stack.isOf(Items.IRON_HOE);
            }
        });
        addSlot(new Slot(this.inventory,10,44,26){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ItemTags.HEAD_ARMOR);
            }
        });
        addSlot(new Slot(this.inventory,11,80,26){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.TOTEM_OF_UNDYING);
            }
        });
        addSlot(new Slot(this.inventory,12,116,26){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.CHEST);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        addSlot(new Slot(this.inventory,13,152,26));
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
        if(!entity.hasBackpack()){
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = entity.getInventory().getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    entity.dropStack(itemStack);
                    entity.getInventory().setStack(i,ItemStack.EMPTY);
                }
            }
        }
        super.onClosed(player);
    }
    @Override
    public boolean canUse(PlayerEntity player) {
        if (this.backpack == entity.hasBackpack()) {
            return !entity.areInventoriesDifferent(inventory);
        }else{
            return false;
        }
    }
}
