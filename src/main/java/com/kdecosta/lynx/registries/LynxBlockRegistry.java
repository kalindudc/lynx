package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.LynxBlock;
import com.kdecosta.lynx.block.LynxBlockItem;
import com.kdecosta.lynx.block.LynxOreBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LynxBlockRegistry {
    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static final Map<String, BlockItem> BLOCK_ITEMS = new HashMap<>();

    // Tag maps
    public static final List<String> MINEABLE_BLOCKS = new ArrayList<>();

    // BLOCKS
    public static final LynxBlock EXAMPLE_BLOCK  = createResource("example_block", "Example Block", FabricBlockSettings.create().strength(4.0f).requiresTool());
    public static final LynxOreBlock URANIUM_ORE = createOreResource(
            "uranium_ore",
            "Uranium Ore",
            FabricBlockSettings.create().strength(4.0f).requiresTool(),
            -64, 0, 20, LynxItemRegistry.RAW_URANIUM);

    public static void registerAll() {
        for (Map.Entry<String, Block> blockEntry: BLOCKS.entrySet()) {
            Block block = blockEntry.getValue();
            Registry.register(Registries.BLOCK, ((LynxBlock) block).getId(), block);
        }

        for (Map.Entry<String, BlockItem> blockItemEntry: BLOCK_ITEMS.entrySet()) {
            BlockItem blockItem = blockItemEntry.getValue();
            Registry.register(Registries.ITEM, ((LynxBlockItem) blockItem).getId(), blockItem);
        }
    }

    public static LynxBlockItem createBlockItem(LynxBlock block, FabricItemSettings settings) {
        LynxBlockItem blockItem = new LynxBlockItem(block, block.getTranslation(), settings);
        BLOCK_ITEMS.put(block.getId().getPath(), blockItem);

        return blockItem;
    }

    public static LynxBlock createResource(String name, String translation, FabricBlockSettings settings) {
        LynxBlock block = new LynxBlock(new Identifier(Lynx.MODID, name), translation, settings);
        createBlockItem(block, new FabricItemSettings());

        BLOCKS.put(name, block);
        MINEABLE_BLOCKS.add(name);

        return block;
    }

    public static LynxOreBlock createOreResource(String name, String translation, FabricBlockSettings settings,
                                                 int minY, int maxY, int veinSize, Item lootDrop) {
        LynxOreBlock block = new LynxOreBlock(new Identifier(Lynx.MODID, name), translation, settings, minY, maxY, veinSize, lootDrop);
        createBlockItem(block, new FabricItemSettings());

        BLOCKS.put(name, block);
        MINEABLE_BLOCKS.add(name);

        return block;
    }
}
