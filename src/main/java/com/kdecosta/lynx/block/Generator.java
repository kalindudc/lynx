package com.kdecosta.lynx.block;

import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import com.kdecosta.lynx.shared.LynxPropertyConstants;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Generator extends LynxMachine {
    public static final float BASE_ENERGY_OUTPUT = 3.0f;

    public Generator(Identifier id, String translation) {
        super(id, translation);
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
