package com.cosmo.psmp;

import com.cosmo.psmp.util.ModCustomAttachedData;
import com.cosmo.psmp.util.ModCustomEntityAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class PSMPAttachmentTypes {
    // Register a type of attached data. This data can be attached to anything, this is only a type
    public static final AttachmentType<ModCustomAttachedData> ABILITIES = AttachmentRegistry.create(
                    Identifier.of(PSMP.MOD_ID,"abilities"),
                    builder->builder // we are using a builder chain here to configure the attachment data type
                            .initializer(()->ModCustomAttachedData.DEFAULT) // a default value to provide if you dont supply one
                            .persistent(ModCustomAttachedData.CODEC).copyOnDeath()// how to save and load the data when the object it is attached to is saved or loaded
            .syncWith(
                    ModCustomAttachedData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                    AttachmentSyncPredicate.all() // who to send the data to
            )
    );
    public static final AttachmentType<ModCustomEntityAttachedData> ENTITIES = AttachmentRegistry.create(
                    Identifier.of(PSMP.MOD_ID,"entities"),
                    builder->builder // we are using a builder chain here to configure the attachment data type
                            .initializer(()->ModCustomEntityAttachedData.DEFAULT) // a default value to provide if you dont supply one
                            .persistent(ModCustomEntityAttachedData.CODEC)// how to save and load the data when the object it is attached to is saved or loaded
            .syncWith(
                    ModCustomEntityAttachedData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                    AttachmentSyncPredicate.all() // who to send the data to
            )
    );

    public static void init() {
        // This empty method can be called from the mod initializer to ensure our component type is registered at mod initialization time
        // ModAttachmentTypes.init();
    }
}
