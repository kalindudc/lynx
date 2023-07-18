package com.kdecosta.lynx.blockentity;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.energy.BurnTimer;
import com.kdecosta.lynx.energy.EnergyUnit;
import com.kdecosta.lynx.registries.LynxBlockEntityRegistry;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.screen.GeneratorScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GeneratorBlockEntity extends LynxBlockEntity {

    private EnergyUnit energy;
    private BurnTimer burnTimer;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, LynxBlockEntityRegistry.BLOCK_ENTITY_TYPES.get(LynxBlockRegistry.GENERATOR), 1);

        this.burnTimer = new BurnTimer();
        this.energy = new EnergyUnit();
    }

    public boolean isGenerating() {
        return this.burnTimer.isTimerDone();
    }

    public boolean isFull() {
        return this.energy.getEnergy() >= LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.getEnergy();
    }

    public EnergyUnit getEnergy() {
        return energy;
    }

    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {

        if (world.isClient) return;
        sendDataToPlayers();

        if (isFull()) {
            if (this.energy.getEnergy() > LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.getEnergy())
                this.energy.setEnergy(LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.getEnergy());

            if (state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
                world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, false));
            }
            return;
        }

        if (isGenerating()) {
            this.burnTimer.tick();
            try {
                this.energy.tick();
            } catch (EnergyUnit.EnergyUnitTooLargeException e) {
                throw new RuntimeException(e);
            }
            Lynx.LOGGER.info(String.format("%d", energy.getEnergy()));
            Lynx.LOGGER.info(String.format("%d", energy.injectionRate()));

            if (!state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
                world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, true));
            }
            markDirty();
            return;
        }

        ItemStack stack = getStack(0);
        if (!stack.isEmpty()) {
            loadFuel();
        } else if (state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
            world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, false));
            this.energy.setInjectionRate(0);
        }
    }

    public boolean isFuel(Item item) {
        return LynxMachineConstants.GENERATOR_BURN_RATE_IN_SECONDS.containsKey(item);
    }

    private void loadFuel() {
        ItemStack stack = getStack(0);
        if (!isFuel(stack.getItem())) return;

        this.burnTimer.setInSeconds(LynxMachineConstants.GENERATOR_BURN_RATE_IN_SECONDS.get(stack.getItem()));
        this.energy.setInjectionRate(LynxMachineConstants.GENERATOR_ENERGY_RATES.get(stack.getItem()));

        stack.setCount(stack.getCount() - 1);
        setStack(0, stack.copy());
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        try {
            nbt.putByteArray("burn_timer", BurnTimer.getBytes(burnTimer));
            nbt.putByteArray("energy", EnergyUnit.getBytes(energy));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Lynx.LOGGER.info("reading nbt");
        super.readNbt(nbt);
        try {
            burnTimer = BurnTimer.fromBytes(nbt.getByteArray("burn_timer"));
            energy = EnergyUnit.fromBytes(nbt.getByteArray("energy"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void sendDataToPlayers() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(nbt);

        getPlayerRegistry().forEach((key, player) -> {
            ServerPlayNetworking.send(player, LynxNetworkingConstants.GENERATOR_PACKET_ID, buf);
        });
    }
}
