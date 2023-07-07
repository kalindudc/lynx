package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

public class LynxBlockItem extends BlockItem implements ILynxResource {

    public LynxBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Identifier getId() {
        return ((LynxBlock) this.getBlock()).getId();
    }
}
