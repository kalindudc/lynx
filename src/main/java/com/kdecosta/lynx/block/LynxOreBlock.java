package com.kdecosta.lynx.block;

import com.kdecosta.lynx.block.base.LynxBlock;
import com.kdecosta.lynx.shared.IHasLootDrop;
import com.kdecosta.lynx.shared.ITypedResource;
import com.kdecosta.lynx.shared.LynxResources;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class LynxOreBlock extends LynxBlock implements IHasLootDrop, ITypedResource {

    private final int minY;
    private final int maxY;
    private final int veinSize;
    private final Item lootDrop;
    private final LynxResources.IBlockResource resourceType;

    public LynxOreBlock(Identifier id, String translation, LynxResources.IBlockResource resourceType, int minY, int maxY, int veinSize, Item lootDrop) {
        super(id, translation, FabricBlockSettings.create().strength(resourceType.getResourceStrength()).requiresTool());
        this.minY = minY;
        this.maxY = maxY;
        this.veinSize = veinSize;
        this.lootDrop = lootDrop;
        this.resourceType = resourceType;
    }

    /**
     * The maximum vein size of this ore block when generating in the world
     *
     * @return the maximum vein size
     */
    public int getVeinSize() {
        return veinSize;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }

    @Override
    public Item getDrop() {
        return lootDrop;
    }

    @Override
    public LynxResources.IResource getResourceType() {
        return resourceType;
    }
}
