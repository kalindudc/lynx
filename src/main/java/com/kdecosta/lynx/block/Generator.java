package com.kdecosta.lynx.block;

import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof GeneratorBlockEntity entity)) return;

        entity.searchAndRegister(world, pos);
    }
}
