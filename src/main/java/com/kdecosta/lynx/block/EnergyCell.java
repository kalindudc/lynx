package com.kdecosta.lynx.block;

import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.EnergyCellBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof EnergyCellBlockEntity entity)) return;

        entity.searchAndRegister(world, pos);
    }
}
