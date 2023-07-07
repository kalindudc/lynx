package com.kdecosta.lynx.block;

import net.minecraft.util.Identifier;

public class LynxOreBlock extends LynxBlock {

    private final int minY;
    private final int maxY;
    private final int veinSize;
    public LynxOreBlock(Identifier id, Settings settings, int minY, int maxY, int veinSize) {
        super(id, settings);
        this.minY = minY;
        this.maxY = maxY;
        this.veinSize = veinSize;
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
}
