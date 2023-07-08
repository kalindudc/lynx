package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.item.LynxItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class LynxItemRegistry {

    public static final Map<String, Item> ITEMS = new HashMap<>();

    public static final LynxItem RAW_URANIUM = createItem("raw_uranium", "Raw Uranium", new FabricItemSettings());
    public static final LynxItem URANIUM_INGOT = createItem("uranium_ingot", "Uranium Ingot", new FabricItemSettings());

    public static void registerAll() {
        for (Map.Entry<String, Item> itemEntry: ITEMS.entrySet()) {
            Item item = itemEntry.getValue();
            Registry.register(Registries.ITEM, ((LynxItem) item).getId(), item);
        }
    }

    public static LynxItem createItem(String name, String translation, FabricItemSettings settings) {
        LynxItem item = new LynxItem(new Identifier(Lynx.MODID, name), translation, settings);
        ITEMS.put(name, item);

        return item;
    }
}
