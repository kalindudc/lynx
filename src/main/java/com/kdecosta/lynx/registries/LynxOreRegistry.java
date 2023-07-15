package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.block.LynxBlock;
import com.kdecosta.lynx.block.LynxOreBlock;
import com.kdecosta.lynx.item.LynxBlockItem;
import com.kdecosta.lynx.item.LynxItem;
import com.kdecosta.lynx.item.LynxResourceItem;
import com.kdecosta.lynx.shared.ITypedResource;
import com.kdecosta.lynx.shared.LynxResources;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LynxOreRegistry {

    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static final Map<String, BlockItem> BLOCK_ITEMS = new HashMap<>();
    public static final Map<String, Item> ITEMS = new HashMap<>();
    public static final Map<Item, List<ItemConvertible>> INGOTS_TO_SMELTABLES = new HashMap<>();

    public static final LynxResourceItem RAW_URANIUM = createItem("raw_uranium", "Raw Uranium",
            new FabricItemSettings(), LynxResources.OreResources.URANIUM, null);
    public static final LynxOreBlock URANIUM_ORE = createBlock(
            "uranium_ore",
            "Uranium Ore",
            LynxResources.OreResources.URANIUM,-64, 0, 20, RAW_URANIUM);
    public static final LynxResourceItem URANIUM_INGOT = createItem("uranium_ingot", "Uranium Ingot",
            new FabricItemSettings(), LynxResources.OreResources.URANIUM,
            Util.make(new ArrayList<>(), list -> {
                list.add(URANIUM_ORE);
                list.add(RAW_URANIUM);
            }));
    public static void registerAll() {
        for (Map.Entry<String, Item> itemEntry: ITEMS.entrySet()) {
            Item item = itemEntry.getValue();
            Registry.register(Registries.ITEM, ((LynxItem) item).getId(), item);
        }

        for (Map.Entry<String, Block> blockEntry: BLOCKS.entrySet()) {
            Block block = blockEntry.getValue();
            Registry.register(Registries.BLOCK, ((LynxBlock) block).getId(), block);
        }

        for (Map.Entry<String, BlockItem> blockItemEntry: BLOCK_ITEMS.entrySet()) {
            BlockItem blockItem = blockItemEntry.getValue();
            Registry.register(Registries.ITEM, ((LynxBlockItem) blockItem).getId(), blockItem);
        }
    }

    public static LynxResourceItem createItem(String name, String translation, FabricItemSettings settings,
                                              LynxResources.IResource  resourceType, List<ItemConvertible> smeltableFrom) {
        LynxResourceItem item = new LynxResourceItem(new Identifier(Lynx.MODID, name), translation, settings, resourceType);
        LynxItemRegistry.ITEMS.put(name, item);
        ITEMS.put(name, item);

        if (smeltableFrom != null) {
            INGOTS_TO_SMELTABLES.put(item, smeltableFrom);
        }
        return item;
    }

    public static LynxOreBlock createBlock(String name, String translation, LynxResources.IBlockResource  resourceType,
                                           int minY, int maxY, int veinSize, Item lootDrop) {
        LynxOreBlock block = new LynxOreBlock(new Identifier(Lynx.MODID, name), translation, resourceType, minY, maxY, veinSize, lootDrop);
        LynxBlockRegistry.createBlock(block);
        BLOCKS.put(name, block);
        BLOCK_ITEMS.put(name, LynxBlockRegistry.BLOCK_ITEMS.get(name));
        return block;
    }
}
