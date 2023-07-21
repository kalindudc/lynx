package com.kdecosta.lynx.block;

import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Generator extends LynxMachine {
    public Generator(Identifier id, String translation) {
        super(id, translation, true);
    }

    @Override
    public void setPropertyDefaults() {
        setDefaultState(getDefaultState().with(LynxPropertyConstants.GENERATING_PROPERTY, false));
    }

    @Override
    public String getPropertyId() {
        return LynxPropertyConstants.GENERATING_PROPERTY_ID;
    }

    @Override
    public BooleanProperty getProperty() {
        return LynxPropertyConstants.GENERATING_PROPERTY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }
}
