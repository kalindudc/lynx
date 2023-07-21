package com.kdecosta.lynx.block.base;

import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.blockentity.base.LynxMachineBlockEntity;
import com.kdecosta.lynx.shared.IHasModelVariants;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.client.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class LynxMachine extends LynxBlock implements IHasModelVariants {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private final boolean facedBlock;

    public LynxMachine(Identifier id, String translation, boolean facedBlock) {
        super(id, translation, FabricBlockSettings.create().strength(4.0f).requiresTool().nonOpaque());

        this.facedBlock = facedBlock;
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
        setPropertyDefaults();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }


    public abstract void setPropertyDefaults();

    public abstract String getPropertyId();

    public abstract BooleanProperty getProperty();

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Override
    public void generateModelVariants(BlockStateModelGenerator blockStateModelGenerator) {
        String suffix = "_" + getPropertyId();

        if (this.facedBlock) {
            generateFacedVariants(blockStateModelGenerator, suffix);
            return;
        }

        Identifier off = TexturedModel.CUBE_ALL.upload(this, blockStateModelGenerator.modelCollector);
        Identifier on = blockStateModelGenerator.createSubModel(this, suffix, Models.CUBE_ALL, TextureMap::all);

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(this)
                        .coordinate(BlockStateModelGenerator.createBooleanModelMap(getProperty(), on, off))
        );
    }

    private void generateFacedVariants(BlockStateModelGenerator blockStateModelGenerator, String suffix) {
        Identifier off = TexturedModel.ORIENTABLE.upload(this, blockStateModelGenerator.modelCollector);
        Identifier on_id = TextureMap.getSubId(this, "_front" + suffix);
        Identifier on = TexturedModel.ORIENTABLE.get(this).textures(textures -> textures.put(TextureKey.FRONT, on_id))
                .upload(this, suffix, blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(this)
                        .coordinate(BlockStateModelGenerator.createBooleanModelMap(getProperty(), on, off))
                        .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(getProperty(), FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker<T>) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory == null) return ActionResult.SUCCESS;

        player.openHandledScreen(screenHandlerFactory);

        if (!(world.getBlockEntity(pos) instanceof LynxBlockEntity entity)) return ActionResult.SUCCESS;
        entity.registerPlayer((ServerPlayerEntity) player);
        return ActionResult.SUCCESS;
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof LynxMachineBlockEntity entity)) return;

        entity.searchAndRegister(world, pos);
    }
}
