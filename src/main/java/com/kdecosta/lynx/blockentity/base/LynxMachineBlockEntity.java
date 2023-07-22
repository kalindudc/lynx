package com.kdecosta.lynx.blockentity.base;

import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.shared.dataunit.EnergyUnit;
import com.kdecosta.lynx.util.DirectionalUtil;
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
    private final HashMap<Direction, Boolean> injectionSides;
    private final HashMap<Direction, Boolean> extractionSides;
    private final HashMap<LynxMachineBlockEntity, Direction> machineToDirection;

    private HashMap<LynxMachineBlockEntity, Producer> producers;
    private HashMap<LynxMachineBlockEntity, BlockPos> consumers;
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
        this.injectionSides = new HashMap<>();
        this.extractionSides = new HashMap<>();
        this.machineToDirection = new HashMap<>();
        setupInjectionAndExtractionSides();
    }

    private void setupInjectionAndExtractionSides() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.injectionSides.put(direction, false);
            this.extractionSides.put(direction, false);
        }
        this.injectionSides.put(Direction.UP, true);
        this.extractionSides.put(Direction.DOWN, true);
    }

    public void setAllSidesInjectable() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.injectionSides.put(direction, true);
        }
    }

    public void setAllSidesExtractable() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.extractionSides.put(direction, true);
        }
    }

    public void searchAndRegister(World world, BlockPos pos) {
        if (world.isClient) return;

        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            BlockPos newPos = pos.add(direction.getVector());
            if (!(world.getBlockEntity(newPos) instanceof LynxMachineBlockEntity entity)) continue;

            if (entity.canConsumeEnergy())
                registerConsumer(entity, direction);
            if (entity.canProduceEnergy())
                registerProducer(entity, direction);
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
            if (!canExtract(this.machineToDirection.get(consumer))) continue;
            if (!consumer.canInject(DirectionalUtil.getOppositeDirection(machineToDirection.get(consumer)))) continue;
            extractRate += consumer.inject(this, rate);
        }
        setExtractionRate(extractRate);
    }

    public void registerConsumer(LynxMachineBlockEntity consumer, Direction direction) {
        if (consumers.containsKey(consumer)) return;

        consumers.put(consumer, consumer.getPos());
        machineToDirection.put(consumer, direction);
        if (this.canProduceEnergy()) consumer.registerProducer(this, DirectionalUtil.getOppositeDirection(direction));
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
        toRemove.forEach(key -> {
            consumers.remove(key);
            machineToDirection.remove(key);
        });

        if (consumers.size() == 0) {
            setExtractionRate(0);
        }
    }

    public long getCurrentExtractionRatePerConsumer() {
        int validConsumers = 0;
        for (LynxMachineBlockEntity consumer : consumers.keySet()) {
            if (!consumer.isFull() &&
                    consumer.canInject(DirectionalUtil.getOppositeDirection(machineToDirection.get(consumer))) &&
                    canExtract(machineToDirection.get(consumer))) {
                validConsumers += 1;
            }
        }
        if (validConsumers == 0) return 0;

        return Math.min(maxExtractionRate, energy.energy()) / validConsumers;
    }

    private boolean canExtract(Direction direction) {
        return this.extractionSides.get(direction);
    }

    private boolean canInject(Direction direction) {
        return this.injectionSides.get(direction);
    }

    public long getExtractionRate() {
        if (consumers.size() == 0) return 0;
        return Math.min(maxExtractionRate, energy.energy());
    }

    public long inject(LynxMachineBlockEntity producer, long rate) {
        if (energy.energy() >= maxEnergy) {
            setInjectionRate(0);
            producers.get(producer).setInjectionRate(0);
            return 0;
        }

        long currRateByProducer = producers.get(producer).getInjectionRate();
        if (rate == currRateByProducer) return rate;

        if (rate < currRateByProducer) {
            setInjectionRate(rate);
            producers.get(producer).setInjectionRate(rate);
            return rate;
        }

        long rateDiff = rate - currRateByProducer;
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

    public void registerProducer(LynxMachineBlockEntity producer, Direction direction) {
        if (!producers.containsKey(producer)) {
            producers.put(producer, new Producer(producer, producer.getPos(), 0));
            machineToDirection.put(producer, direction);
            if (this.canConsumeEnergy())
                producer.registerConsumer(this, DirectionalUtil.getOppositeDirection(direction));
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

        long rate = energy.injectionRate();
        for (LynxMachineBlockEntity producer : toRemove) {
            rate -= producers.get(producer).getInjectionRate();
            producers.remove(producer);
            machineToDirection.remove(producer);
        }
        energy.setInjectionRate(rate);
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        try {
            nbt.putByteArray("energy", EnergyUnit.getBytes(energy));
            nbt.putLong("max_energy", maxEnergy);
            nbt.putLong("max_extraction_rate", maxExtractionRate);
            nbt.putLong("max_injection_rate", maxInjectionRate);
            storeSidesDataToNbt(nbt);
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
            readSidesDataFromNbt(nbt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void storeSidesDataToNbt(NbtCompound nbt) {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            nbt.putBoolean(String.format("injection_side_%s", direction.getName()), this.injectionSides.get(direction));
            nbt.putBoolean(String.format("extraction_side_%s", direction.getName()), this.extractionSides.get(direction));
        }
    }

    public void readSidesDataFromNbt(NbtCompound nbt) {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.injectionSides.put(direction, nbt.getBoolean(String.format("injection_side_%s", direction.getName())));
            this.extractionSides.put(direction, nbt.getBoolean(String.format("extraction_side_%s", direction.getName())));
        }
    }

    public void updateSides(NbtCompound nbt) {
        readSidesDataFromNbt(nbt);
        setExtractionRate(0);
        setInjectionRate(0);
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
