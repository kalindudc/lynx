package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.EnergyCell;
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
    public static final Map<String, Block> TRANSLUCENT_BLOCKS = new HashMap<>();
    public static final Map<String, BlockItem> BLOCK_ITEMS = new HashMap<>();

    // Tag maps
    public static final List<String> MINEABLE_BLOCKS = new ArrayList<>();

    // Machines
    public static final LynxMachine GENERATOR = (LynxMachine) createBlock(
            new Generator(
                    new Identifier(Lynx.MODID, "generator"),
                    "Generator"), false);
    public static final LynxMachine ENERGY_CELL = (LynxMachine) createBlock(
            new EnergyCell(
                    new Identifier(Lynx.MODID, "energy_cell"),
                    "Energy Cell"), true);

    public static void registerAll() {
        BLOCKS.forEach((name, block) -> {
            Registry.register(Registries.BLOCK, ((LynxBlock) block).getId(), block);
        });

        BLOCK_ITEMS.forEach((name, item) -> {
            Registry.register(Registries.ITEM, ((LynxBlockItem) item).getId(), item);
        });
    }

    public static LynxBlock createBlock(LynxBlock block, boolean isTranslucent) {
        createBlockItem(block, new FabricItemSettings());

        BLOCKS.put(block.getId().getPath(), block);
        MINEABLE_BLOCKS.add(block.getId().getPath());
        if (isTranslucent) TRANSLUCENT_BLOCKS.put(block.getId().getPath(), block);

        return block;
    }

    public static LynxBlockItem createBlockItem(LynxBlock block, FabricItemSettings settings) {
        LynxBlockItem blockItem = new LynxBlockItem(block, block.getTranslation(), settings);
        BLOCK_ITEMS.put(block.getId().getPath(), blockItem);

        return blockItem;
    }
}
