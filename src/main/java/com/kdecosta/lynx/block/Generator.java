package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.IHasModelVariants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.data.client.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Generator extends LynxBlock implements IHasModelVariants {

    public static final float BASE_ENERGY_OUTPUT = 3.0f;
    public static final String PROPERTY_ID = "generating";

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final BooleanProperty GENERATING = BooleanProperty.of(PROPERTY_ID);

    public Generator(Identifier id, String translation, Settings settings) {
        super(id, translation, settings);
        setDefaultState(getDefaultState().with(GENERATING, false).with(FACING, Direction.NORTH));
    }

    public Property<Boolean> getProperty() {
        return GENERATING;
    }

    @Override
    public void generateModelVariants(BlockStateModelGenerator blockStateModelGenerator) {
        String suffix = "_" + PROPERTY_ID;

        Identifier off = TexturedModel.ORIENTABLE.upload(this, blockStateModelGenerator.modelCollector);
        Identifier on_id = TextureMap.getSubId(this, "_front" + suffix);
        Identifier on = TexturedModel.ORIENTABLE.get(this).textures(textures -> textures.put(TextureKey.FRONT, on_id))
                .upload(this, suffix, blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(this)
                        .coordinate(BlockStateModelGenerator.createBooleanModelMap(GENERATING, on, off))
                        .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GENERATING, FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
        world.setBlockState(pos, state.with(GENERATING, true));
        return ActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.getBlockState(pos).get(GENERATING)) {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
            world.spawnEntity(lightningEntity);
        }

        world.setBlockState(pos, state.with(GENERATING, false));
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
}
