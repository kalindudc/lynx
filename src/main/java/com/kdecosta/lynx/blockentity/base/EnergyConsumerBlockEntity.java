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

public abstract class EnergyConsumerBlockEntity extends LynxBlockEntity {

    private final long maxInjectionRate;
    private final long maxExtractionRate;
    private final long maxEnergy;
    private final HashMap<EnergyProducerBlockEntity, Producer> producers;

    private EnergyUnit energy;

    public EnergyConsumerBlockEntity(BlockPos pos, BlockState state, BlockEntityType<? extends BlockEntity> blockEntityType, int itemStackSize,
                                     long maxEnergy, long maxInjectionRate, long maxExtractionRate) {
        super(pos, state, blockEntityType, itemStackSize);

        this.producers = new HashMap<>();

        this.maxInjectionRate = maxInjectionRate;
        this.maxExtractionRate = maxExtractionRate;
        this.maxEnergy = maxEnergy;
        this.energy = new EnergyUnit();
    }

    public void searchAndRegister(World world, BlockPos pos) {
        if (world.isClient) return;

        for (BlockPos cordOffset : LynxConstants.SEARCH_CORDS) {
            BlockPos newPos = new BlockPos(
                    pos.getX() + cordOffset.getX(),
                    pos.getY() + cordOffset.getY(),
                    pos.getZ() + cordOffset.getZ()
            );
            if (!(world.getBlockEntity(newPos) instanceof EnergyProducerBlockEntity producer)) continue;
            registerProducer(producer);
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        if (world.isClient) return;

        verifyProducers();

        if (this.energy.energy() >= maxEnergy) {
            this.energy.setEnergy(maxEnergy);
            stopInjection();
        }

        try {
            this.energy.tick();
        } catch (EnergyUnit.EnergyUnitTooLargeException e) {
            throw new RuntimeException(e);
        }
    }

    public long inject(EnergyProducerBlockEntity producer, long rate) {
        if (energy.energy() >= maxEnergy) return 0;

        long currRateByProducer = 0;
        if (producers.containsKey(producer)) currRateByProducer = producers.get(producer).getInjectionRate();

        long rateDiff = currRateByProducer - rate;
        long newRate = rate;
        if (energy.injectionRate() + rateDiff > maxInjectionRate) {
            newRate = maxInjectionRate - (energy.injectionRate() - currRateByProducer);
        }
        producers.get(producer).setInjectionRate(newRate);
        energy.setInjectionRate(energy.injectionRate() - currRateByProducer + newRate);

        markDirty();
        return newRate;
    }

    public boolean isFull() {
        return this.energy.energy() >= maxEnergy;
    }

    private void stopInjection() {
        this.energy.setInjectionRate(0);
        producers.forEach((producer, producerData) -> {
            producerData.setInjectionRate(0);
        });
        markDirty();
    }

    public void registerProducer(EnergyProducerBlockEntity producer) {
        if (!producers.containsKey(producer)) {
            producers.put(producer, new Producer(producer, producer.getPos(), 0));
            producer.registerConsumer(this, this.getPos());
        }
    }

    public void deregisterProducer(EnergyProducerBlockEntity producer) {
        producers.remove(producer);
    }

    public void verifyProducers() {
        if (world == null) return;

        List<EnergyProducerBlockEntity> toRemove = new ArrayList<>();
        producers.forEach((producer, producerData) -> {
            if (world.getBlockEntity(producerData.getPos()) == null) toRemove.add(producer);
        });

        toRemove.forEach(producer -> {
            long rate = energy.injectionRate() - producers.get(producer).getInjectionRate();
            energy.setInjectionRate(rate);
            producers.remove(producer);
        });
        markDirty();
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

    public EnergyUnit getEnergy() {
        return energy;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public long getMaxExtractionRate() {
        return maxExtractionRate;
    }

    private static class Producer {
        private EnergyProducerBlockEntity producer;
        private BlockPos pos;
        private long injectionRate;

        public Producer(EnergyProducerBlockEntity producer, BlockPos pos, long injectionRate) {
            this.injectionRate = injectionRate;
            this.producer = producer;
            this.pos = pos;
        }

        public EnergyProducerBlockEntity getProducer() {
            return producer;
        }

        public void setProducer(EnergyProducerBlockEntity producer) {
            this.producer = producer;
        }

        public BlockPos getPos() {
            return pos;
        }

        public void setPos(BlockPos pos) {
            this.pos = pos;
        }

        public long getInjectionRate() {
            return injectionRate;
        }

        public void setInjectionRate(long injectionRate) {
            this.injectionRate = injectionRate;
        }
    }
}
