package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.BlockItem;

import java.util.Map;

public class LynxModelGeneratorProvider extends FabricModelProvider {
    public LynxModelGeneratorProvider(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (Map.Entry<String, Block> entry: LynxBlockRegistry.BLOCKS.entrySet()) {
            blockStateModelGenerator.registerSimpleCubeAll(entry.getValue());
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // ...
    }
}
