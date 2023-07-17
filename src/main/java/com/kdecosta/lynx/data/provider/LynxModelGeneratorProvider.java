package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.item.LynxItem;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.registries.LynxItemRegistry;
import com.kdecosta.lynx.shared.IHasModelVariants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;

import java.util.Map;

public class LynxModelGeneratorProvider extends FabricModelProvider {
    public LynxModelGeneratorProvider(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (Map.Entry<String, Block> entry : LynxBlockRegistry.BLOCKS.entrySet()) {
            Block block = entry.getValue();
            if (block instanceof IHasModelVariants variantBlock) {
                variantBlock.generateModelVariants(blockStateModelGenerator);
            } else {
                blockStateModelGenerator.registerSimpleCubeAll(block);
            }
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (Map.Entry<String, Item> itemEntry : LynxItemRegistry.ITEMS.entrySet()) {
            LynxItem item = (LynxItem) itemEntry.getValue();
            itemModelGenerator.register(item, Models.GENERATED);
        }
    }
}
