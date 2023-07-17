package com.kdecosta.lynx.networking;

import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.screen.base.LynxScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;

public class LynxClientPacketHandler {
    public static void registerClientPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(LynxNetworkingConstants.GENERATOR_PACKET_ID, (client, handler, buf, responseSender) -> {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBytes(buf.readBytes(buf.readableBytes()));

            MinecraftClient.getInstance().execute(() -> {
                Screen currentScreen = MinecraftClient.getInstance().currentScreen;
                if (currentScreen instanceof LynxScreen screen) {
                    screen.handlePacket(data);
                }
            });
        });
    }
}
