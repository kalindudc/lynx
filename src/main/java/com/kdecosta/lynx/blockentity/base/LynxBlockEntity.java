package com.kdecosta.lynx.blockentity.base;

import com.kdecosta.lynx.inventory.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class LynxBlockEntity extends BlockEntity implements BlockEntityTicker<LynxBlockEntity>,
        ImplementedInventory, ExtendedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items;

    private HashMap<String, ServerPlayerEntity> playerRegistry;

    public LynxBlockEntity(BlockPos pos, BlockState state, BlockEntityType<? extends BlockEntity> blockEntityType, int itemStackSize) {
        super(blockEntityType, pos, state);
        this.items = DefaultedList.ofSize(itemStackSize, ItemStack.EMPTY);
        playerRegistry = new HashMap<>();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public abstract void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity);

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public abstract ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player);

    public void registerPlayer(ServerPlayerEntity player) {
        if (!playerRegistry.containsKey(player.getUuidAsString())) {
            playerRegistry.put(player.getUuidAsString(), player);
        }
    }

    public void deregisterPlayer(ServerPlayerEntity player) {
        if (playerRegistry.containsKey(player.getUuidAsString())) {
            playerRegistry.remove(player.getUuidAsString());
        }
    }

    public HashMap<String, ServerPlayerEntity> getPlayerRegistry() {
        return playerRegistry;
    }

    public abstract void sendDataToPlayers();

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
