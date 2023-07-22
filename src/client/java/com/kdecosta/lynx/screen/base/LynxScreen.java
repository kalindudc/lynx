package com.kdecosta.lynx.screen.base;

import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class LynxScreen extends HandledScreen<LynxScreenHandler> {
    private BlockPos pos;

    public LynxScreen(LynxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        pos = handler.getBlockPos();
    }

    public BlockPos getPos() {
        return pos;
    }

    public abstract Identifier getTexture();

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, getTexture());
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(getTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    public abstract void handlePacket(PacketByteBuf buf);

    @Override
    public void removed() {
        super.removed();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        ClientPlayNetworking.send(LynxNetworkingConstants.DEREGISTER_PLAYER_PACKET_ID, buf);
    }
}
