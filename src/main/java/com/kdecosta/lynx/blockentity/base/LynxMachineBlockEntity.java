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
    private final HashMap<Direction, FaceType> faceTypes;
    private final HashMap<LynxMachineBlockEntity, Direction> neighbours;
    private final HashMap<LynxMachineBlockEntity, Long> injectionRates;

    private EnergyUnit energy;
    private long maxInjectionRate;
    private long maxExtractionRate;
    private long maxEnergy;
    private boolean triggerSearch;

    public LynxMachineBlockEntity(BlockPos pos, BlockState state, BlockEntityType<? extends BlockEntity> blockEntityType, int itemStackSize,
                                  long maxEnergy, long maxInjectionRate, long maxExtractionRate) {
        super(pos, state, blockEntityType, itemStackSize);

        this.maxInjectionRate = maxInjectionRate;
        this.maxExtractionRate = maxExtractionRate;
        this.maxEnergy = maxEnergy;
        this.energy = new EnergyUnit(128000);
        this.triggerSearch = false;
        this.faceTypes = new HashMap<>();
        this.neighbours = new HashMap<>();
        this.injectionRates = new HashMap<>();
        setupFaceTypes();
    }

    private void setupFaceTypes() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.faceTypes.put(direction, FaceType.NONE);
        }
        this.faceTypes.put(Direction.UP, FaceType.INJECTION);
        this.faceTypes.put(Direction.DOWN, FaceType.EXTRACTION);
    }

    public void setAllSidesInjectable() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.faceTypes.put(direction, FaceType.INJECTION);
        }
    }

    public void setAllSidesExtractable() {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.faceTypes.put(direction, FaceType.EXTRACTION);
        }
    }

    public void searchAndRegister(World world, BlockPos pos) {
        if (world.isClient) return;

        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            BlockPos newPos = pos.add(direction.getVector());
            if (!(world.getBlockEntity(newPos) instanceof LynxMachineBlockEntity entity)) continue;

            registerNeighbour(entity, direction);
        }
    }

    public void registerNeighbour(LynxMachineBlockEntity entity, Direction direction) {
        if (neighbours.containsKey(entity)) return;
        this.neighbours.put(entity, direction);
        entity.registerNeighbour(this, direction.getOpposite());
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

        verifyNeighbours(world);
        distributeEnergy();

        if (isFull()) {
            if (this.energy.energy() > maxEnergy) this.energy.setEnergy(maxEnergy);
            stopInjection();
        }

        try {
            this.energy.tick();
        } catch (EnergyUnit.EnergyUnitTooLargeException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyNeighbours(World world) {
        List<LynxMachineBlockEntity> toRemove = new ArrayList<>();
        neighbours.forEach((neighbour, dir) -> {
            if (world.getBlockEntity(neighbour.getPos()) == null) toRemove.add(neighbour);
        });

        long rate = energy.injectionRate();
        for (LynxMachineBlockEntity neighbour : toRemove) {
            neighbours.remove(neighbour);
            if (!injectionRates.containsKey(neighbour)) continue;
            rate -= injectionRates.get(neighbour);
            injectionRates.remove(neighbour);
        }
        energy.setInjectionRate(rate);
        markDirty();
    }

    public void setInjectionRate(long injectionRate) {
        this.energy.setInjectionRate(injectionRate);
    }

    public void setExtractionRate(long extractionRate) {
        this.energy.setExtractionRate(extractionRate);
    }

    private void distributeEnergy() {
        if (neighbours.size() == 0) return;

        List<LynxMachineBlockEntity> validConsumers = getValidConsumers();
        if (validConsumers.size() == 0) {
            setExtractionRate(0);
            return;
        }

        long extractRate = 0;
        long rate = Math.min(maxExtractionRate, energy.energy()) / validConsumers.size();

        for (LynxMachineBlockEntity consumer : validConsumers) {
            extractRate += consumer.inject(this, rate);
        }
        setExtractionRate(extractRate);
    }

    public List<LynxMachineBlockEntity> getValidConsumers() {
        List<LynxMachineBlockEntity> validConsumers = new ArrayList<>();
        for (LynxMachineBlockEntity neighbour : neighbours.keySet()) {
            if (neighbour.isFull()) continue;
            if (!neighbour.isInjectable(neighbours.get(neighbour).getOpposite())) continue;
            if (!canExtract(neighbours.get(neighbour))) continue;

            validConsumers.add(neighbour);
        }

        return validConsumers;
    }

    private boolean canExtract(Direction direction) {
        return this.faceTypes.get(direction) == FaceType.EXTRACTION;
    }

    private boolean isInjectable(Direction direction) {
        return this.faceTypes.get(direction) == FaceType.INJECTION;
    }

    public long inject(LynxMachineBlockEntity producer, long rate) {
        if (isFull()) return 0;

        long currRateByProducer = 0;
        if (injectionRates.containsKey(producer)) currRateByProducer = injectionRates.get(producer);
        if (rate == currRateByProducer) return rate;

        if (rate < currRateByProducer) {
            setInjectionRate(rate);
            injectionRates.put(producer, rate);
            return rate;
        }

        long rateDiff = rate - currRateByProducer;
        long newRate = rate;
        if (energy.injectionRate() + rateDiff > maxInjectionRate) {
            newRate = maxInjectionRate - (energy.injectionRate() - currRateByProducer);
        }
        injectionRates.put(producer, newRate);
        energy.setInjectionRate(energy.injectionRate() - currRateByProducer + newRate);

        markDirty();
        return newRate;
    }

    public boolean isFull() {
        return this.energy.energy() >= maxEnergy;
    }

    private void stopInjection() {
        this.energy.setInjectionRate(0);
        for (LynxMachineBlockEntity producers : injectionRates.keySet()) {
            injectionRates.put(producers, 0L);
        }
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
        for (Direction direction : this.faceTypes.keySet()) {
            nbt.putString(String.format("face_type_%s", direction.getName()), this.faceTypes.get(direction).toString());
        }
    }

    public void readSidesDataFromNbt(NbtCompound nbt) {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.faceTypes.put(direction, FaceType.fromString(nbt.getString(String.format("face_type_%s", direction.getName()))));
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

    public enum FaceType {
        NONE("none"),
        INJECTION("injection"),
        EXTRACTION("extraction");

        private final String type;

        FaceType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            return getType();
        }

        public static FaceType fromString(String type) {
            if (type.equalsIgnoreCase("injection")) {
                return INJECTION;
            }
            if (type.equalsIgnoreCase("extraction")) {
                return EXTRACTION;
            }
            return NONE;
        }
    }
}
