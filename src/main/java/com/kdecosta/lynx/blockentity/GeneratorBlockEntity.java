package com.kdecosta.lynx.blockentity;

import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.blockentity.base.LynxMachineBlockEntity;
import com.kdecosta.lynx.energy.BurnTimer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GeneratorBlockEntity extends LynxMachineBlockEntity {
    public static final long MAX_EXTRACTION_RATE = 256;

    private BurnTimer burnTimer;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, LynxBlockEntityRegistry.BLOCK_ENTITY_TYPES.get(LynxBlockRegistry.GENERATOR), 1,
                LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.energy(), 0, MAX_EXTRACTION_RATE);

        this.burnTimer = new BurnTimer();
    }

    public boolean isGenerating() {
        return this.burnTimer.isTimerDone();
    }

    public boolean isFull() {
        return this.getEnergy().energy() >= LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.energy();
    }

    public void stopGenerating(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, false));
        setInjectionRate(0);
        this.burnTimer.reset();
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        super.tick(world, pos, state, blockEntity);

        if (world.isClient) return;
        verifyConsumers();
        sendDataToPlayers();
        handleFuelAndEnergyGeneration(world, pos, state, blockEntity);
    }

    private void handleFuelAndEnergyGeneration(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        if (isFull()) {
            if (this.getEnergy().energy() > LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.energy())
                this.getEnergy().setEnergy(LynxMachineConstants.GENERATOR_ENERGY_CAPACITY.energy());

            if (state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
                stopGenerating(world, pos, state);
            }
            return;
        }

        if (isGenerating()) {
            this.burnTimer.tick();

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
            stopGenerating(world, pos, state);
        }
    }

    public boolean isFuel(Item item) {
        return LynxMachineConstants.GENERATOR_BURN_RATE_IN_SECONDS.containsKey(item);
    }

    private void loadFuel() {
        ItemStack stack = getStack(0);
        if (!isFuel(stack.getItem())) return;

        this.burnTimer.setInSeconds(LynxMachineConstants.GENERATOR_BURN_RATE_IN_SECONDS.get(stack.getItem()));
        setInjectionRate(LynxMachineConstants.GENERATOR_ENERGY_RATES.get(stack.getItem()));

        this.getStack(0).setCount(stack.getCount() - 1);
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        try {
            nbt.putByteArray("burn_timer", BurnTimer.getBytes(burnTimer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try {
            burnTimer = BurnTimer.fromBytes(nbt.getByteArray("burn_timer"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, playerInventory, this);
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
