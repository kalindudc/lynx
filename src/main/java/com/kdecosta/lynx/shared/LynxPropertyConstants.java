package com.kdecosta.lynx.shared;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class LynxPropertyConstants {
    public static final String GENERATING_PROPERTY_ID = "generating";

    public static final BooleanProperty GENERATING_PROPERTY = BooleanProperty.of(GENERATING_PROPERTY_ID);
}
