package com.kdecosta.lynx;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("P.A.N.D.A");

	public static final Block EXAMPLE_BLOCK  = new Block(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final BlockItem EXAMPLE_BLOCK_ITEM  = new BlockItem(EXAMPLE_BLOCK, new FabricItemSettings());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		Registry.register(Registries.BLOCK, new Identifier("lynx", "example_block"), EXAMPLE_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("lynx", "example_block"), EXAMPLE_BLOCK_ITEM);
	}
}
