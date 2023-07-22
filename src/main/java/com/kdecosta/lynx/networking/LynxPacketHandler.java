package com.kdecosta.lynx.networking;

import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.blockentity.base.LynxMachineBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LynxPacketHandler {

    public static Direction getDirection(String dir) {
        return switch (dir) {
            case ("up") -> Direction.UP;
            case ("down") -> Direction.DOWN;
            case ("south") -> Direction.SOUTH;
            case ("east") -> Direction.EAST;
            case ("west") -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

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

        ServerPlayNetworking.registerGlobalReceiver(LynxNetworkingConstants.UPDATE_SIDES_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            NbtCompound nbt = buf.readNbt();
            if (nbt == null) return;
            BlockPos blockPos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));

            // Handle the received packet on the server side
            server.execute(() -> {
                World world = player.getEntityWorld();
                if (world == null) return;
                if (!(world.getBlockEntity(blockPos) instanceof LynxMachineBlockEntity entity)) return;
                entity.updateSides(nbt);
            });
        });
    }
}
