package com.kdecosta.lynx.block;

import com.kdecosta.lynx.shared.ILynxResource;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LynxBlock extends BlockWithEntity implements ILynxResource {

    private final Identifier id;
    private final String translation;

    public LynxBlock(Identifier id, String translation, Settings settings) {
        super(settings);
        this.id = id;
        this.translation = translation;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String getTranslation() {
        return translation;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
