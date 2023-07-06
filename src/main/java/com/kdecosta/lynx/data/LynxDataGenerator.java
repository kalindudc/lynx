package com.kdecosta.lynx.data;

import com.kdecosta.lynx.data.provider.LynxBlockLootTableProvider;
import com.kdecosta.lynx.data.provider.LynxBlockTagProvider;
import com.kdecosta.lynx.data.provider.LynxModelGeneratorProvider;
import com.kdecosta.lynx.data.provider.LynxItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class LynxDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack lynxPack = fabricDataGenerator.createPack();
        lynxPack.addProvider(LynxItemTagProvider::new);
        lynxPack.addProvider(LynxBlockTagProvider::new);
        lynxPack.addProvider(LynxBlockLootTableProvider::new);
        lynxPack.addProvider(LynxModelGeneratorProvider::new);
    }
}