package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.ExampleMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class LynxBlockLootTableProvider extends FabricBlockLootTableProvider {
    public LynxBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ExampleMod.EXAMPLE_BLOCK, ExampleMod.EXAMPLE_BLOCK_ITEM);
    }
}