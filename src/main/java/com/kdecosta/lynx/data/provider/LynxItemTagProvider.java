package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.item.LynxItem;
import com.kdecosta.lynx.registries.LynxItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LynxItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public LynxItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // ...
    }
}
