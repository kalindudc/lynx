package com.kdecosta.lynx;

import com.kdecosta.lynx.networking.LynxPacketHandler;
import com.kdecosta.lynx.registries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lynx implements ModInitializer {
    public static final String MODID = "lynx";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final Identifier ITEM_GROUP_ID = new Identifier(MODID, "item_group");
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(LynxOreRegistry.URANIUM_INGOT))
            .displayName(Text.translatable(MODID + ".item_group"))
            .build();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Lynx ...");

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_ID, ITEM_GROUP);

        LynxItemRegistry.registerAll();
        LynxBlockRegistry.registerAll();
        LynxOreRegistry.registerAll();
        LynxBlockEntityRegistry.registerAll();
        LynxScreenHandlerRegistry.registerAll();

        // register creative mode tab with all the items
        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, ITEM_GROUP_ID)).register(content -> {
            LynxBlockRegistry.BLOCK_ITEMS.forEach((name, blockItem) -> {
                content.add(blockItem);
            });

            LynxItemRegistry.ITEMS.forEach((name, item) -> {
                content.add(item);
            });
        });

        LynxPacketHandler.registerPacketHandlers();
    }
}
