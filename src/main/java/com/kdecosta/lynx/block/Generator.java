package com.kdecosta.lynx.block;

import com.google.gson.JsonPrimitive;
import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.shared.IHasModelVariants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class Generator extends LynxBlock implements IHasModelVariants {

    public static final float BASE_ENERGY_OUTPUT = 3.0f;
    public static final String PROPERTY_ID = "generating";

    private static final BooleanProperty GENERATING = BooleanProperty.of(PROPERTY_ID);

    public Generator(Identifier id, String translation, Settings settings) {
        super(id, translation, settings);
        setDefaultState(getDefaultState().with(GENERATING, false));
    }

    public Property<Boolean> getProperty() {
        return GENERATING;
    }

    @Override
    public void generateModelVariants(BlockStateModelGenerator blockStateModelGenerator) {
        String suffix = "_" + PROPERTY_ID;

        Identifier off = TexturedModel.CUBE_ALL.upload(this, blockStateModelGenerator.modelCollector);
        Identifier on_id = TextureMap.getSubId(this, suffix);
        Identifier on = TexturedModel.CUBE_ALL.get(this).textures(textures -> textures.put(TextureKey.ALL, on_id))
                .upload(this, suffix, blockStateModelGenerator.modelCollector);


        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(this)
                        .coordinate(BlockStateVariantMap.create(GENERATING)
                                .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, on))
                                .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, off)))
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GENERATING);
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
            LightningEntity lightningEntity = (LightningEntity) EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
            world.spawnEntity(lightningEntity);
        }

        world.setBlockState(pos, state.with(GENERATING, false));
    }
}
