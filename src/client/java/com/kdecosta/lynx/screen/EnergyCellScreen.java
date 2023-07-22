package com.kdecosta.lynx.screen;

import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.api.LynxNetworkingConstants;
import com.kdecosta.lynx.api.LynxScreenConstants;
import com.kdecosta.lynx.screen.base.LynxScreen;
import com.kdecosta.lynx.screen.base.LynxScreenHandler;
import com.kdecosta.lynx.screen.widget.MachineSettingsWidget;
import com.kdecosta.lynx.shared.dataunit.EnergyUnit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

public class EnergyCellScreen extends LynxScreen {
    private HashMap<Direction, Boolean> injectionSides;
    private HashMap<Direction, Boolean> extractionSides;

    private EnergyUnit energy;
    private MachineSettingsWidget settingsWidget;

    public EnergyCellScreen(LynxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.energy = new EnergyUnit();
        this.injectionSides = new HashMap<>();
        this.extractionSides = new HashMap<>();
        this.settingsWidget = new MachineSettingsWidget(0, 0);
    }

    @Override
    protected void init() {
        super.init();
        this.settingsWidget.setX(this.x + backgroundWidth + 2);
        this.settingsWidget.setY(this.y + 20);
        this.settingsWidget.setClient(this.client);
        this.settingsWidget.init();
        this.addDrawableChild(new TexturedButtonWidget(
                        this.settingsWidget.getX(),
                        this.settingsWidget.getY() - 20,
                        20,
                        18,
                        0,
                        0,
                        19,
                        LynxScreenConstants.MACHINE_SETTINGS_BUTTON_TEXTURE, button -> {
                    this.settingsWidget.toggleOpen();
                    //button.setPosition(this.settingsWidget.getX(), this.settingsWidget.getY() - 20);
                })
        );
        this.addSelectableChild(this.settingsWidget);
        this.setInitialFocus(this.settingsWidget);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double) left || mouseY < (double) top || mouseX >= (double) (left + this.backgroundWidth) || mouseY >= (double) (top + this.backgroundHeight);
        return this.settingsWidget.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.settingsWidget.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.settingsWidget);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.settingsWidget.update();
        if (this.settingsWidget.shouldSendPacketUpdate()) {
            sendUpdateSidesPacket();
            this.settingsWidget.setSendPacketUpdate(false);
        }
    }

    private void sendUpdateSidesPacket() {
        this.settingsWidget.getSides().forEach((dir, type) -> {
            if (type == MachineSettingsWidget.SideType.INJECTION) {
                this.injectionSides.put(dir, true);
                this.extractionSides.put(dir, false);
            } else if (type == MachineSettingsWidget.SideType.EXTRACTION) {
                this.injectionSides.put(dir, false);
                this.extractionSides.put(dir, true);
            } else {
                this.injectionSides.put(dir, false);
                this.extractionSides.put(dir, false);
            }
        });

        NbtCompound nbt = new NbtCompound();
        nbt.putInt("x", getPos().getX());
        nbt.putInt("y", getPos().getY());
        nbt.putInt("z", getPos().getZ());
        storeSidesDataToNbt(nbt);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(nbt);
        ClientPlayNetworking.send(LynxNetworkingConstants.UPDATE_SIDES_PACKET_ID, buf);
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

        if (this.settingsWidget.isOpen()) {
            this.settingsWidget.render(context, mouseX, mouseY, delta);
        }

        context.drawText(client.textRenderer, String.format("Energy: %d", energy.energy()), x + 73, (y + 26) + (12 * 0), 11141120, false);
        context.drawText(client.textRenderer, String.format("Injection: %d", energy.injectionRate()), x + 73, (y + 26) + (12 * 1), 11141120, false);
        context.drawText(client.textRenderer, String.format("Extraction: %d", energy.extractionRate()), x + 73, (y + 26) + (12 * 2), 11141120, false);
    }

    public void handlePacket(PacketByteBuf buf) {
        NbtCompound nbt = buf.readNbt();

        if (nbt == null) return;

        try {
            energy = EnergyUnit.fromBytes(nbt.getByteArray("energy"));
            readSidesDataFromNbt(nbt);
            if (!this.settingsWidget.shouldSendPacketUpdate())
                this.settingsWidget.updateSides(this.injectionSides, this.extractionSides);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void storeSidesDataToNbt(NbtCompound nbt) {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            nbt.putBoolean(String.format("injection_side_%s", direction.getName()), this.injectionSides.get(direction));
            nbt.putBoolean(String.format("extraction_side_%s", direction.getName()), this.extractionSides.get(direction));
        }
    }

    private void readSidesDataFromNbt(NbtCompound nbt) {
        for (Direction direction : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.injectionSides.put(direction, nbt.getBoolean(String.format("injection_side_%s", direction.getName())));
            this.extractionSides.put(direction, nbt.getBoolean(String.format("extraction_side_%s", direction.getName())));
        }
    }
}
