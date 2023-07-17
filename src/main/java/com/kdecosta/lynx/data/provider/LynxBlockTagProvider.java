package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.registries.LynxBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class LynxBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public LynxBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        for (String blockName : LynxBlockRegistry.MINEABLE_BLOCKS) {
            Block block = LynxBlockRegistry.BLOCKS.get(blockName);
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(block);
            getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(block);
        }
    }
}
