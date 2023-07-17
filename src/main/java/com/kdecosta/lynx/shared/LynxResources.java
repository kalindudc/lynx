package com.kdecosta.lynx.shared;

import net.minecraft.block.Block;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public abstract class LynxResources {

    public interface IResource {
        String getResourceName();
    }

    public interface IBlockResource extends IResource {

        float getResourceStrength();

        TagKey<Block> getMineableType();

        TagKey<Block> getMineableTool();
    }

    public enum OreResources implements IBlockResource {
        URANIUM("uranium", BlockTags.NEEDS_IRON_TOOL);

        private final String resourceName;
        private final float resourceStrength;
        private final TagKey<Block> mineableType;
        private final TagKey<Block> mineableTool;

        OreResources(String resourceName, TagKey<Block> mineableTool) {
            this.resourceName = resourceName;
            this.resourceStrength = 4.0f;
            this.mineableType = BlockTags.PICKAXE_MINEABLE;
            this.mineableTool = mineableTool;
        }

        @Override
        public String getResourceName() {
            return resourceName;
        }

        @Override
        public float getResourceStrength() {
            return resourceStrength;
        }

        @Override
        public TagKey<Block> getMineableType() {
            return mineableType;
        }

        @Override
        public TagKey<Block> getMineableTool() {
            return mineableTool;
        }
    }
}
