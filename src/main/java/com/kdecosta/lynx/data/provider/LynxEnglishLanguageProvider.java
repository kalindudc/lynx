package com.kdecosta.lynx.data.provider;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.LynxBlock;
import com.kdecosta.lynx.item.LynxItem;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.registries.LynxItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.Map;

public class LynxEnglishLanguageProvider extends FabricLanguageProvider {
    public LynxEnglishLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(RegistryKey.of(RegistryKeys.ITEM_GROUP, Lynx.ITEM_GROUP_ID), "Lynx");

        for (Map.Entry<String, Block> entry: LynxBlockRegistry.BLOCKS.entrySet()) {
            LynxBlock block = (LynxBlock) entry.getValue();
            translationBuilder.add(block, block.getTranslation());
        }

        for (Map.Entry<String, Item> entry: LynxItemRegistry.ITEMS.entrySet()) {
            LynxItem item = (LynxItem) entry.getValue();
            translationBuilder.add(item, item.getTranslation());
        }
    }
}
