package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.screen.EnergyCellScreenHandler;
import com.kdecosta.lynx.screen.GeneratorScreenHandler;
import com.kdecosta.lynx.screen.base.LynxScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class LynxScreenHandlerRegistry {
    public static final HashMap<Block, LynxScreenHandlerRegistry.LynxScreenHandlerRegistryItem> REGISTRY = new HashMap<>();
    public static final HashMap<Block, ScreenHandlerType<LynxScreenHandler>> SCREEN_HANDLER_TYPES = new HashMap<>();

    static {
        registerScreenHandler(
                new Identifier(Lynx.MODID, "generator_screen_handler"),
                LynxBlockRegistry.GENERATOR,
                new ExtendedScreenHandlerType<ScreenHandler>(GeneratorScreenHandler::new)
        );
        registerScreenHandler(
                new Identifier(Lynx.MODID, "energy_cell_screen_handler"),
                LynxBlockRegistry.ENERGY_CELL,
                new ExtendedScreenHandlerType<ScreenHandler>(EnergyCellScreenHandler::new)
        );
    }

    public static void registerAll() {
        REGISTRY.forEach((block, registryItem) -> {
            ScreenHandlerType<? extends ScreenHandler> type = Registry.register(
                    Registries.SCREEN_HANDLER,
                    registryItem.id(),
                    registryItem.type()
            );
            SCREEN_HANDLER_TYPES.put(registryItem.block, (ScreenHandlerType<LynxScreenHandler>) type);
        });
    }

    public static void registerScreenHandler(Identifier id, Block block, ScreenHandlerType<? extends ScreenHandler> type) {
        LynxScreenHandlerRegistryItem registryItem = new LynxScreenHandlerRegistryItem(id, block, type);
        REGISTRY.put(block, registryItem);
    }

    private record LynxScreenHandlerRegistryItem(Identifier id, Block block,
                                                 ScreenHandlerType<? extends ScreenHandler> type) {

    }
}
