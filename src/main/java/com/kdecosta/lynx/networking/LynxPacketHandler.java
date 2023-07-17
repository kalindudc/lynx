package com.kdecosta.lynx.networking;

import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LynxPacketHandler {
    public static void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(LynxNetworkingConstants.DEREGISTER_PLAYER_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos blockPos = buf.readBlockPos();

            // Handle the received packet on the server side
            server.execute(() -> {
                World world = player.getEntityWorld();
                if (world == null) return;
                if (!(world.getBlockEntity(blockPos) instanceof LynxBlockEntity entity)) return;
                entity.deregisterPlayer(player);
            });
        });
    }
}
