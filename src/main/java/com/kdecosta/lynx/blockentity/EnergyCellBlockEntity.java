package com.kdecosta.lynx.blockentity;

import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.blockentity.base.LynxMachineBlockEntity;
import com.kdecosta.lynx.registries.LynxBlockEntityRegistry;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.screen.EnergyCellScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyCellBlockEntity extends LynxMachineBlockEntity {
    public static final long MAX_RATE = 1024;

    public EnergyCellBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, LynxBlockEntityRegistry.BLOCK_ENTITY_TYPES.get(LynxBlockRegistry.ENERGY_CELL), 1,
                LynxMachineConstants.ENERGY_CELL_MAX_CAPACITY.energy(), MAX_RATE, MAX_RATE);
        setAllSidesInjectable();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        super.tick(world, pos, state, blockEntity);
        if (world.isClient) return;

        if (this.getEnergy().energy() > 0 && !state.get(LynxPropertyConstants.POWERED_PROPERTY)) {
            world.setBlockState(pos, state.with(LynxPropertyConstants.POWERED_PROPERTY, true));
        } else if (this.getEnergy().energy() == 0 && state.get(LynxPropertyConstants.POWERED_PROPERTY)) {
            world.setBlockState(pos, state.with(LynxPropertyConstants.POWERED_PROPERTY, false));
        }

        sendDataToPlayers();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EnergyCellScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void sendDataToPlayers() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(nbt);

        getPlayerRegistry().forEach((key, player) -> {
            ServerPlayNetworking.send(player, LynxNetworkingConstants.ENERGY_CELL_PACKET_ID, buf);
        });
    }

    @Override
    public void updateSides(NbtCompound nbt) {
        super.updateSides(nbt);

        NbtCompound _nbt = new NbtCompound();
        _nbt.putBoolean("update_packet_success", true);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(_nbt);

        getPlayerRegistry().forEach((key, player) -> {
            ServerPlayNetworking.send(player, LynxNetworkingConstants.ENERGY_CELL_PACKET_ID, buf);
        });
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        // do stuff
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // do stuff
    }

}
