package com.kdecosta.lynx.energy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEnergyConsumer {

    long getMaxInjectionRate();

    long setInjectionRate(long injectionRate);

    void registerProducer(IEnergyProducer producer, BlockPos pos);

    void deregisterProducer(IEnergyProducer producer);

    void searchAndRegister(World world, BlockPos pos);

    void verifyProducers();
}
