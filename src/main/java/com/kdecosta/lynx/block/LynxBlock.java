package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class LynxBlock extends Block implements ILynxResource {

    private final Identifier id;

    public LynxBlock(Identifier id, Settings settings) {
        super(settings);
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
