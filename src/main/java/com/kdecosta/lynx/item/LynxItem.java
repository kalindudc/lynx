package com.kdecosta.lynx.item;

import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class LynxItem extends Item implements ILynxResource {

    private final Identifier id;
    private final String translation;

    public LynxItem(Identifier id, String translation, Settings settings) {
        super(settings);
        this.id = id;
        this.translation = translation;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}
