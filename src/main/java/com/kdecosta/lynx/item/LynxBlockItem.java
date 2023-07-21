package com.kdecosta.lynx.item;

import com.kdecosta.lynx.block.base.LynxBlock;
import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

public class LynxBlockItem extends BlockItem implements ILynxResource {

    private final String translation;

    public LynxBlockItem(Block block, String translation, Settings settings) {
        super(block, settings);
        this.translation = translation;
    }

    @Override
    public Identifier getId() {
        return ((LynxBlock) this.getBlock()).getId();
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}
