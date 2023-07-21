package com.kdecosta.lynx.energy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEnergyProducer {
    void registerConsumer(IEnergyConsumer consumer, BlockPos pos);

    void deregisterConsumer(IEnergyConsumer consumer);

    void searchAndRegister(World world, BlockPos pos);

    void verifyConsumers();

    long getMaxExtractionRate();
}
