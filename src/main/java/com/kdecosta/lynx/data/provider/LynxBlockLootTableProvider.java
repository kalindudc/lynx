package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import java.util.Map;

public class LynxBlockLootTableProvider extends FabricBlockLootTableProvider {
    public LynxBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        for (Map.Entry<String, Block> entry: LynxBlockRegistry.BLOCKS.entrySet()) {
            Block block = entry.getValue();
            BlockItem blockItem = LynxBlockRegistry.BLOCK_ITEMS.get(entry.getKey());
            addDrop(block, blockItem);
        }
    }
}