package com.kdecosta.lynx.blockentity;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.Generator;
import com.kdecosta.lynx.registries.LynxBlockEntityRegistry;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.shared.LynxPropertyConstants;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements BlockEntityTicker<GeneratorBlockEntity> {
    private float remainingFuel;

    private float energy;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(LynxBlockEntityRegistry.BLOCK_ENTITY_TYPES.get(LynxBlockRegistry.GENERATOR), pos, state);
        this.remainingFuel = 0f;
        this.energy = 0f;
    }

    public boolean isGenerating() {
        return this.remainingFuel > 0;
    }

    public float getRemainingFuel() {
        return remainingFuel;
    }

    public void setRemainingFuel(float remainingFuel) {
        this.remainingFuel = remainingFuel;
        markDirty();
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
        markDirty();
    }

    public void loadFuel() {
        this.remainingFuel = 20f;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("remaining_fuel", remainingFuel);
        nbt.putFloat("energy", energy);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        remainingFuel = nbt.getFloat("remaining_fuel");
        energy = nbt.getFloat("energy");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
        if (isGenerating()) {
            this.remainingFuel -= 1f;
            this.energy += Generator.BASE_ENERGY_OUTPUT;
            markDirty();
        }

        if (state.get(LynxPropertyConstants.GENERATING_PROPERTY) && this.remainingFuel <= 0f) {
            loadFuel();
        }

        Lynx.LOGGER.info(String.format("Fuel: %f", remainingFuel));
        Lynx.LOGGER.info(String.format("Energy: %f", energy));
    }
}
