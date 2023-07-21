package com.kdecosta.lynx.blockentity.base;

import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.shared.dataunit.EnergyUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class LynxMachineBlockEntity extends LynxBlockEntity {
    private final HashMap<LynxMachineBlockEntity, Producer> producers;
    private final HashMap<LynxMachineBlockEntity, BlockPos> consumers;

    private EnergyUnit energy;
    private long maxInjectionRate;
    private long maxExtractionRate;
    private long maxEnergy;
    private boolean triggerSearch;

    public LynxMachineBlockEntity(BlockPos pos, BlockState state, BlockEntityType<? extends BlockEntity> blockEntityType, int itemStackSize,
                                  long maxEnergy, long maxInjectionRate, long maxExtractionRate) {
        super(pos, state, blockEntityType, itemStackSize);

        this.producers = new HashMap<>();
        this.consumers = new HashMap<>();

        this.maxInjectionRate = maxInjectionRate;
        this.maxExtractionRate = maxExtractionRate;
        this.maxEnergy = maxEnergy;
        this.energy = new EnergyUnit();
        this.triggerSearch = false;
    }

    public void searchAndRegister(World world, BlockPos pos) {
        if (world.isClient) return;

        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            BlockPos newPos = pos.add(direction.getVector());
            if (!(world.getBlockEntity(newPos) instanceof LynxMachineBlockEntity entity)) continue;

            if (entity.canConsumeEnergy()) registerConsumer(entity, entity.getPos());
            if (entity.canProduceEnergy()) registerProducer(entity);
        }
    }

    public boolean canProduceEnergy() {
        return this.maxExtractionRate > 0;
    }

    public boolean canConsumeEnergy() {
        return this.maxInjectionRate > 0;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {
        if (world.isClient) return;

        if (this.triggerSearch) {
            searchAndRegister(world, pos);
            this.triggerSearch = false;
        }

        verifyProducers();
        verifyConsumers();
        distributeEnergy();

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

    public void setInjectionRate(long injectionRate) {
        this.energy.setInjectionRate(injectionRate);
    }

    public void setExtractionRate(long extractionRate) {
        this.energy.setExtractionRate(extractionRate);
    }

    private void distributeEnergy() {
        if (consumers.size() == 0) return;

        long rate = getCurrentExtractionRatePerConsumer();
        long extractRate = 0;

        for (LynxMachineBlockEntity consumer : consumers.keySet()) {
            if (consumer.isFull()) continue;
            extractRate += consumer.inject(this, rate);
        }
        setExtractionRate(extractRate);
    }

    public void registerConsumer(LynxMachineBlockEntity consumer, BlockPos pos) {
        if (consumers.containsKey(consumer)) return;

        consumers.put(consumer, pos);
        if (this.canProduceEnergy()) consumer.registerProducer(this);
    }

    public void deregisterConsumer(LynxMachineBlockEntity consumer) {
        consumers.remove(consumer);
    }

    public void verifyConsumers() {
        if (world == null) return;

        List<LynxMachineBlockEntity> toRemove = new ArrayList<>();
        consumers.forEach((consumer, pos) -> {
            if (world.getBlockEntity(pos) == null) toRemove.add(consumer);
        });

        long extractionRate = this.energy.extractionRate();
        for (LynxMachineBlockEntity consumer : toRemove) {
            extractionRate -= getCurrentExtractionRatePerConsumer();
            consumers.remove(consumer);
        }
        this.energy.setExtractionRate(extractionRate);
    }

    public long getCurrentExtractionRatePerConsumer() {
        int validConsumers = 0;
        for (LynxMachineBlockEntity consumer : consumers.keySet()) {
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

    public long inject(LynxMachineBlockEntity producer, long rate) {
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

    public void registerProducer(LynxMachineBlockEntity producer) {
        if (!producers.containsKey(producer)) {
            producers.put(producer, new Producer(producer, producer.getPos(), 0));
            if (this.canConsumeEnergy()) producer.registerConsumer(this, this.getPos());
        }
    }

    public void deregisterProducer(LynxMachineBlockEntity producer) {
        producers.remove(producer);
    }

    public void verifyProducers() {
        if (world == null) return;

        List<LynxMachineBlockEntity> toRemove = new ArrayList<>();
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
            nbt.putLong("max_energy", maxEnergy);
            nbt.putLong("max_extraction_rate", maxExtractionRate);
            nbt.putLong("max_injection_rate", maxInjectionRate);
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
            maxEnergy = nbt.getLong("max_energy");
            maxExtractionRate = nbt.getLong("max_extraction_rate");
            maxInjectionRate = nbt.getLong("max_injection_rate");
            energy.setExtractionRate(0);
            energy.setInjectionRate(0);
            triggerSearch = true;
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
        private LynxMachineBlockEntity producer;
        private BlockPos pos;
        private long injectionRate;

        public Producer(LynxMachineBlockEntity producer, BlockPos pos, long injectionRate) {
            this.injectionRate = injectionRate;
            this.producer = producer;
            this.pos = pos;
        }

        public LynxMachineBlockEntity getProducer() {
            return producer;
        }

        public void setProducer(LynxMachineBlockEntity producer) {
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
