package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.item.LynxItem;
import com.kdecosta.lynx.registries.LynxOreRegistry;
import com.kdecosta.lynx.shared.ITypedResource;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;
import java.util.function.Consumer;

public class LynxRecipeProvider extends FabricRecipeProvider {
    public LynxRecipeProvider(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // ore smelting
        LynxOreRegistry.INGOTS_TO_SMELTABLES.forEach((ingot, smeltables) -> {
            String group = ((LynxItem) ingot).getId().getPath();
            RecipeProvider.offerSmelting(exporter, smeltables, RecipeCategory.MISC, ingot, 1f, 210, group);
            RecipeProvider.offerBlasting(exporter, smeltables, RecipeCategory.MISC, ingot, 1f, 90, group);
        });
    }
}