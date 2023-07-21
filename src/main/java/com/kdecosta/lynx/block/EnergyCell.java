package com.kdecosta.lynx.block;

import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.EnergyCellBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EnergyCell extends LynxMachine {
    public EnergyCell(Identifier id, String translation) {
        super(id, translation, false);
    }

    @Override
    public void setPropertyDefaults() {
        setDefaultState(getDefaultState().with(LynxPropertyConstants.POWERED_PROPERTY, false));
    }

    @Override
    public String getPropertyId() {
        return LynxPropertyConstants.POWERED_PROPERTY_ID;
    }

    @Override
    public BooleanProperty getProperty() {
        return LynxPropertyConstants.POWERED_PROPERTY;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCellBlockEntity(pos, state);
    }
}
