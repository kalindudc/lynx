package com.kdecosta.lynx.api;

import net.minecraft.state.property.BooleanProperty;

public class LynxPropertyConstants {
    public static final String GENERATING_PROPERTY_ID = "generating";
    public static final String POWERED_PROPERTY_ID = "powered";

    public static final BooleanProperty GENERATING_PROPERTY = BooleanProperty.of(GENERATING_PROPERTY_ID);
    public static final BooleanProperty POWERED_PROPERTY = BooleanProperty.of(POWERED_PROPERTY_ID);
}
