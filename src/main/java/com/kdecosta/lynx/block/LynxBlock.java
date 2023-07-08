package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class LynxBlock extends Block implements ILynxResource {

    private final Identifier id;
    private final String translation;

    public LynxBlock(Identifier id, String translation, Settings settings) {
        super(settings);
        this.id = id;
        this.translation = translation;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}
