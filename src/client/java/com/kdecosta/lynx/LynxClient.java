package com.kdecosta.lynx;

import com.kdecosta.lynx.networking.LynxClientPacketHandler;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.registries.LynxScreenHandlerRegistry;
import com.kdecosta.lynx.screen.EnergyCellScreen;
import com.kdecosta.lynx.screen.GeneratorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class LynxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(LynxScreenHandlerRegistry.SCREEN_HANDLER_TYPES.get(LynxBlockRegistry.GENERATOR), GeneratorScreen::new);
        HandledScreens.register(LynxScreenHandlerRegistry.SCREEN_HANDLER_TYPES.get(LynxBlockRegistry.ENERGY_CELL), EnergyCellScreen::new);

        LynxClientPacketHandler.registerClientPacketHandlers();

        LynxBlockRegistry.TRANSLUCENT_BLOCKS.forEach((name, block) -> {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        });
    }
}
