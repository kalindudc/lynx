package com.kdecosta.lynx.blockentity.base;

import com.kdecosta.lynx.api.LynxConstants;
import com.kdecosta.lynx.energy.EnergyUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class EnergyProducerBlockEntity extends LynxBlockEntity {

    private final long maxExtractionRate;
    private final HashMap<EnergyConsumerBlockEntity, BlockPos> consumers;

    private EnergyUnit energy;

    public EnergyProducerBlockEntity(BlockPos pos, BlockState state, BlockEntityType<? extends BlockEntity> blockEntityType, int itemStackSize,
                                     long maxExtractionRate) {
        super(pos, state, blockEntityType, itemStackSize);
        this.maxExtractionRate = maxExtractionRate;
        this.energy = new EnergyUnit();
        this.consumers = new HashMap<>();
    }

    public EnergyUnit getEnergy() {
        return this.energy;
    }

    public void setInjectionRate(long injectionRate) {
        this.energy.setInjectionRate(injectionRate);
    }

    public void setExtractionRate(long extractionRate) {
        this.energy.setExtractionRate(extractionRate);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        if (world.isClient) return;

        verifyConsumers();
        distributeEnergy();
        try {
            this.energy.tick();
        } catch (EnergyUnit.EnergyUnitTooLargeException e) {
            throw new RuntimeException(e);
        }
    }

    private void distributeEnergy() {
        if (consumers.size() == 0) return;

        long rate = getCurrentExtractionRatePerConsumer();
        long extractRate = 0;

        for (EnergyConsumerBlockEntity consumer : consumers.keySet()) {
            if (consumer.isFull()) continue;
            extractRate += consumer.inject(this, rate);
        }
        setExtractionRate(extractRate);
    }

    public void registerConsumer(EnergyConsumerBlockEntity consumer, BlockPos pos) {
        if (consumers.containsKey(consumer)) return;

        consumers.put(consumer, pos);
        consumer.registerProducer(this);
    }

    public void deregisterConsumer(EnergyConsumerBlockEntity consumer) {
        consumers.remove(consumer);
    }

    public void searchAndRegister(World world, BlockPos pos) {
        if (world.isClient) return;

        for (BlockPos cordOffset : LynxConstants.SEARCH_CORDS) {
            BlockPos newPos = new BlockPos(
                    pos.getX() + cordOffset.getX(),
                    pos.getY() + cordOffset.getY(),
                    pos.getZ() + cordOffset.getZ()
            );
            if (!(world.getBlockEntity(newPos) instanceof EnergyConsumerBlockEntity consumer)) continue;
            registerConsumer(consumer, newPos);
        }
    }

    public void verifyConsumers() {
        if (world == null) return;

        List<EnergyConsumerBlockEntity> toRemove = new ArrayList<>();
        consumers.forEach((consumer, pos) -> {
            if (world.getBlockEntity(pos) == null) toRemove.add(consumer);
        });

        long extractionRate = this.energy.extractionRate();
        for (EnergyConsumerBlockEntity consumer : toRemove) {
            extractionRate -= getCurrentExtractionRatePerConsumer();
            consumers.remove(consumer);
        }
        this.energy.setExtractionRate(extractionRate);
    }

    public long getCurrentExtractionRatePerConsumer() {
        int validConsumers = 0;
        for (EnergyConsumerBlockEntity consumer : consumers.keySet()) {
            if (!consumer.isFull()) {
                validConsumers += 1;
            }
        }
        if (validConsumers == 0) return 0;

        return Math.min(maxExtractionRate, energy.energy()) / validConsumers;
    }

    public long getExtractionRate() {
        if (consumers.size() == 0) return 0;
        return Math.min(maxExtractionRate, energy.energy());
    }

    public long getMaxExtractionRate() {
        return maxExtractionRate;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        try {
            nbt.putByteArray("energy", EnergyUnit.getBytes(energy));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try {
            energy = EnergyUnit.fromBytes(nbt.getByteArray("energy"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
