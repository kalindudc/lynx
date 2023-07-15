package com.kdecosta.lynx.item;

import com.kdecosta.lynx.shared.ITypedResource;
import com.kdecosta.lynx.shared.LynxResources;
import net.minecraft.util.Identifier;

public class LynxResourceItem extends LynxItem implements ITypedResource {

    private final LynxResources.IResource resourceType;
    public LynxResourceItem(Identifier id, String translation, Settings settings, LynxResources.IResource resourceType) {
        super(id, translation, settings);
        this.resourceType = resourceType;
    }

    @Override
    public LynxResources.IResource getResourceType() {
        return resourceType;
    }
}
