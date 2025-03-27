package com.cosmo.psmp.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record TeleportLocationAttachedData (String world,BlockPos pos){
    public static final Codec<TeleportLocationAttachedData> CODEC = RecordCodecBuilder.create(teleportLocationInstance -> teleportLocationInstance.group(
            Codec.STRING.fieldOf("world").forGetter( TeleportLocationAttachedData::world),
            BlockPos.CODEC.fieldOf("pos").forGetter( TeleportLocationAttachedData::pos)
    ).apply(teleportLocationInstance, TeleportLocationAttachedData::new)); // all the values we defined above are passed to the factory method we reference here
    // SomeClass::new is a reference to a constructor. so in this case the list of floats is passed to the constructor

    // A packet codec is used to convert the object into a byte buffer for sending over the network or converting back into an object
    // There are better ways to do this, such as using PacketCodec.tuple to build the read/write methods,
    // but for simplicity of the example, this will do.
    // https://wiki.fabricmc.net/tutorial:networking#networking_in_1205
    public static PacketCodec<ByteBuf, TeleportLocationAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    // A default value we can use as an "empty" or reset data component
    // it uses List.of() which creates an empty, immutable list.
}
