package com.kdecosta.lynx.screen;

import com.kdecosta.lynx.api.LynxScreenConstants;
import com.kdecosta.lynx.blockentity.GeneratorBlockEntity;
import com.kdecosta.lynx.screen.base.LynxScreen;
import com.kdecosta.lynx.screen.base.LynxScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GeneratorScreen extends LynxScreen {

    private float energy;
    private float remainingFuel;

    public GeneratorScreen(LynxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.energy = 0f;
        this.remainingFuel = 0f;
    }

    @Override
    public Identifier getTexture() {
        return LynxScreenConstants.GENERATOR_TEXTURE;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (!(handler instanceof GeneratorScreenHandler generatorScreenHandler)) return;
        if (client == null) return;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if (handler.getInventory() instanceof GeneratorBlockEntity entity) {
            context.drawText(client.textRenderer, String.format("%f", entity.getEnergy()), x + 150, y + 54, 11141120, false);
        }
        context.drawText(client.textRenderer, String.format("%f", energy), x + 150, y + 18, 11141120, false);
        context.drawText(client.textRenderer, String.format("%f", remainingFuel), x + 150, y + 36, 11141120, false);
    }

    public void handlePacket(PacketByteBuf buf) {
        NbtCompound nbt = buf.readNbt();

        if (nbt == null) return;
        this.energy = nbt.getFloat("energy");
        this.remainingFuel = nbt.getFloat("remaining_fuel");
    }
}
