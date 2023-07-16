package com.kdecosta.lynx.registries;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class LynxBlockEntityRegistry {
    public static final HashMap<Block, LynxBlockEntityRegistryItem> REGISTRY = new HashMap<>();
    public static final HashMap<Block, BlockEntityType<? extends BlockEntity>> BLOCK_ENTITY_TYPES = new HashMap<>();

    static {
        registerBlockEntity(
            new Identifier(Lynx.MODID, "generator_block_entity"),
            LynxBlockRegistry.GENERATOR,
            GeneratorBlockEntity::new
        );
    }
    public static void registerAll() {
        REGISTRY.forEach((block, registryItem) -> {
            BlockEntityType<? extends BlockEntity> blockEntityType = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                registryItem.getId(),
                FabricBlockEntityTypeBuilder.create(registryItem.getBlockEntityFactory(), block).build()
            );
            BLOCK_ENTITY_TYPES.put(block, blockEntityType);
        });
    }

    public static void registerBlockEntity(Identifier id, Block block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntityFactory) {
        LynxBlockEntityRegistryItem registryItem = new LynxBlockEntityRegistryItem(id, block, blockEntityFactory);
        REGISTRY.put(block, registryItem);
    }

    private static class LynxBlockEntityRegistryItem {
        private final Identifier id;
        private final Block block;
        private final FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntityFactory;

        private LynxBlockEntityRegistryItem(Identifier id, Block block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntityFactory) {
            this.id = id;
            this.block = block;
            this.blockEntityFactory = blockEntityFactory;
        }

        public Identifier getId() {
            return id;
        }

        public Block getBlock() {
            return block;
        }

        public FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> getBlockEntityFactory() {
            return blockEntityFactory;
        }
    }
}
