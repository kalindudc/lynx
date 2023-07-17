package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.Generator;
import com.kdecosta.lynx.block.LynxBlock;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.item.LynxBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
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

    // Machines
    public static final LynxMachine GENERATOR = (LynxMachine) createBlock(
            new Generator(
                    new Identifier(Lynx.MODID, "generator"),
                    "Generator"));

    public static void registerAll() {
        for (Map.Entry<String, Block> blockEntry : BLOCKS.entrySet()) {
            Block block = blockEntry.getValue();
            Registry.register(Registries.BLOCK, ((LynxBlock) block).getId(), block);
        }

        for (Map.Entry<String, BlockItem> blockItemEntry : BLOCK_ITEMS.entrySet()) {
            BlockItem blockItem = blockItemEntry.getValue();
            Registry.register(Registries.ITEM, ((LynxBlockItem) blockItem).getId(), blockItem);
        }
    }

    public static LynxBlock createBlock(LynxBlock block) {
        createBlockItem(block, new FabricItemSettings());

        BLOCKS.put(block.getId().getPath(), block);
        MINEABLE_BLOCKS.add(block.getId().getPath());

        return block;
    }

    public static LynxBlockItem createBlockItem(LynxBlock block, FabricItemSettings settings) {
        LynxBlockItem blockItem = new LynxBlockItem(block, block.getTranslation(), settings);
        BLOCK_ITEMS.put(block.getId().getPath(), blockItem);

        return blockItem;
    }
}
