package com.kdecosta.lynx.block;

import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.base.LynxMachine;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
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

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory == null) return ActionResult.SUCCESS;

        player.openHandledScreen(screenHandlerFactory);
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock().equals(newState.getBlock())) return;
        if (!(world.getBlockEntity(pos) instanceof LynxBlockEntity entity)) return;

        ItemScatterer.spawn(world, pos, entity);
        world.updateComparators(pos, this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
