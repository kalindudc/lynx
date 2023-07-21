package com.kdecosta.lynx.api;

import net.minecraft.util.math.BlockPos;

public class LynxConstants {
    public static final BlockPos[] SEARCH_CORDS = new BlockPos[]{
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
    };
}
