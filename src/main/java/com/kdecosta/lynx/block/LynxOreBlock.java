package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.IHasLootDrop;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class LynxOreBlock extends LynxBlock implements IHasLootDrop {

    private final int minY;
    private final int maxY;
    private final int veinSize;
    private final Item lootDrop;

    public LynxOreBlock(Identifier id, String translation, Settings settings, int minY, int maxY, int veinSize, Item lootDrop) {
        super(id, translation, settings);
        this.minY = minY;
        this.maxY = maxY;
        this.veinSize = veinSize;
        this.lootDrop = lootDrop;
    }

    /**
     * The maximum vein size of this ore block when generating in the world
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
}
