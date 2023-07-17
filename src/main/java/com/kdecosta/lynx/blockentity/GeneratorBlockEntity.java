package com.kdecosta.lynx.blockentity;

import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.api.LynxPropertyConstants;
import com.kdecosta.lynx.block.Generator;
import com.kdecosta.lynx.blockentity.base.LynxBlockEntity;
import com.kdecosta.lynx.registries.LynxBlockEntityRegistry;
import com.kdecosta.lynx.registries.LynxBlockRegistry;
import com.kdecosta.lynx.screen.GeneratorScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends LynxBlockEntity {
    private float remainingFuel;
    private float energy;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, LynxBlockEntityRegistry.BLOCK_ENTITY_TYPES.get(LynxBlockRegistry.GENERATOR), 1);

        this.remainingFuel = 0f;
        this.energy = 0f;
    }

    public boolean isGenerating() {
        return this.remainingFuel > 0f;
    }

    public float getRemainingFuel() {
        return remainingFuel;
    }

    public void setRemainingFuel(float remainingFuel) {
        this.remainingFuel = remainingFuel;
        markDirty();
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
        markDirty();
    }

    public void tick(World world, BlockPos pos, BlockState state, LynxBlockEntity blockEntity) {

        if (world.isClient) return;
        if (isGenerating()) {
            this.remainingFuel -= 1f;
            this.energy += Generator.BASE_ENERGY_OUTPUT;

            if (!state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
                world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, true));
            }
            markDirty();
            sendDataToPlayers();
            return;
        }

        ItemStack stack = getStack(0);
        if (!stack.isEmpty()) {
            loadFuel();
        } else if (state.get(LynxPropertyConstants.GENERATING_PROPERTY)) {
            world.setBlockState(pos, state.with(LynxPropertyConstants.GENERATING_PROPERTY, false));
        }
    }

    public boolean isFuel(Item item) {
        return (item.equals(Items.COAL) ||
                item.equals(Items.COAL_BLOCK) ||
                item.equals(Items.CHARCOAL)
        );
    }

    public void handleItemInjection(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (getStack(0).isEmpty()) {
            // try to add item
            if (isFuel(itemStack.getItem())) {
                setStack(0, itemStack.copy());
                player.getStackInHand(hand).setCount(0);
            }
        } else {
            // try to add the same item
            ItemStack curentItemStack = getStack(0);
            Item currentItemOnPlayer = itemStack.getItem();

            if (curentItemStack.getCount() < curentItemStack.getMaxCount() && curentItemStack.getItem().equals(currentItemOnPlayer)) {
                int maxAcceptable = curentItemStack.getMaxCount() - curentItemStack.getCount();
                int acceptable = Math.min(maxAcceptable, itemStack.getCount());

                curentItemStack.setCount(curentItemStack.getCount() + acceptable);
                setStack(0, curentItemStack.copy());
                player.getStackInHand(hand).setCount(itemStack.getCount() - acceptable);
            }
        }
    }

    private void loadFuel() {
        this.remainingFuel = 20f * 5f;

        ItemStack stack = getStack(0);
        stack.setCount(stack.getCount() - 1);

        setStack(0, stack.copy());
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("remaining_fuel", remainingFuel);
        nbt.putFloat("energy", energy);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        remainingFuel = nbt.getFloat("remaining_fuel");
        energy = nbt.getFloat("energy");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void sendDataToPlayers() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("energy", energy);
        nbt.putFloat("remaining_fuel", remainingFuel);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(nbt);

        getPlayerRegistry().forEach((key, player) -> {
            ServerPlayNetworking.send(player, LynxNetworkingConstants.GENERATOR_PACKET_ID, buf);
        });
    }
}
