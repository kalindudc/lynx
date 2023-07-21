package com.kdecosta.lynx.screen;

import com.kdecosta.lynx.api.LynxScreenConstants;
import com.kdecosta.lynx.energy.EnergyUnit;
import com.kdecosta.lynx.screen.base.LynxScreen;
import com.kdecosta.lynx.screen.base.LynxScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnergyCellScreen extends LynxScreen {

    private EnergyUnit energy;

    public EnergyCellScreen(LynxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.energy = new EnergyUnit();
    }

    @Override
    public Identifier getTexture() {
        return LynxScreenConstants.ENERGY_CELL_TEXTURE;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (client == null) return;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawText(client.textRenderer, String.format("Energy: %d", energy.energy()), x + 73, (y + 26) + (12 * 0), 11141120, false);
        context.drawText(client.textRenderer, String.format("Injection: %d", energy.injectionRate()), x + 73, (y + 26) + (12 * 1), 11141120, false);
        context.drawText(client.textRenderer, String.format("Extraction: %d", energy.extractionRate()), x + 73, (y + 26) + (12 * 2), 11141120, false);
    }

    public void handlePacket(PacketByteBuf buf) {
        NbtCompound nbt = buf.readNbt();

        if (nbt == null) return;

        try {
            energy = EnergyUnit.fromBytes(nbt.getByteArray("energy"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
