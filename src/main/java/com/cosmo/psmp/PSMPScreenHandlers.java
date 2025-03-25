package com.cosmo.psmp;

import com.cosmo.psmp.networking.IntPayload;
import com.cosmo.psmp.screen.MinionScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class PSMPScreenHandlers {
    public static final ScreenHandlerType<MinionScreenHandler> MINION_SCREEN_HANDLER =
            register("minion_screen_handler",MinionScreenHandler::new, IntPayload.PACKET_CODEC);

    public static <T extends ScreenHandler,D extends CustomPayload>ExtendedScreenHandlerType<T,D> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T,D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER,Identifier.of(PSMP.MOD_ID,name), new ExtendedScreenHandlerType<>(factory,codec));
    }

    public static void init(){
    }
}
