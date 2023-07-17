package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.shared.IHasLootDrop;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Map;

public class LynxBlockLootTableProvider extends FabricBlockLootTableProvider {
    public LynxBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        for (Map.Entry<String, Block> entry : LynxBlockRegistry.BLOCKS.entrySet()) {
            Block block = entry.getValue();
            Item item = LynxBlockRegistry.BLOCK_ITEMS.get(entry.getKey());

            if (block instanceof IHasLootDrop) {
                item = ((IHasLootDrop) block).getDrop();
            }

            addDrop(block, item);

        }
    }
}