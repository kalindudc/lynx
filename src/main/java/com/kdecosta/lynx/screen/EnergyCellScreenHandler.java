package com.kdecosta.lynx.screen;

import com.kdecosta.lynx.api.LynxScreenConstants;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.registries.LynxScreenHandlerRegistry;
import com.kdecosta.lynx.screen.base.LynxScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class EnergyCellScreenHandler extends LynxScreenHandler {
    private BlockPos pos;

    public EnergyCellScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory);
        this.pos = buf.readBlockPos();
    }

    public EnergyCellScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
        pos = BlockPos.ORIGIN;
    }

    public EnergyCellScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(LynxScreenHandlerRegistry.SCREEN_HANDLER_TYPES.get(LynxBlockRegistry.ENERGY_CELL), syncId, playerInventory, inventory);
    }

    @Override
    public void handleGUISlots() {
        this.addSlot(new Slot(getInventory(), 0, LynxScreenConstants.ENERGY_CELL_TEXTURE_SLOT_CORDS[0], LynxScreenConstants.ENERGY_CELL_TEXTURE_SLOT_CORDS[1]));
    }

    @Override
    public BlockPos getBlockPos() {
        return pos;
    }
}
