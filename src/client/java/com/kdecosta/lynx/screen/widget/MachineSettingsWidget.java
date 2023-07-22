package com.kdecosta.lynx.screen.widget;

import com.kdecosta.lynx.Lynx;
import com.kdecosta.lynx.api.LynxMachineConstants;
import com.kdecosta.lynx.api.LynxScreenConstants;
import com.kdecosta.lynx.blockentity.base.LynxMachineBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class MachineSettingsWidget implements Drawable, Element, Selectable {
    public static final int WIDTH = 86;
    public static final int HEIGHT = 136;
    public static final int BUTTON_SIZE = 18;

    private int x;
    private int y;
    private boolean isOpen;
    private MinecraftClient client;
    private HashMap<Direction, LynxMachineBlockEntity.FaceType> faceTypes;
    private HashMap<Direction, TexturedButtonWidget> sideButtons;
    private HashMap<Direction, TexturedButtonWidget> noneSideButtons;
    private HashMap<Direction, TexturedButtonWidget> extractionSideButtons;
    private HashMap<Direction, TexturedButtonWidget> injectionSideButtons;
    private boolean sendPacketUpdate;

    public MachineSettingsWidget(int x, int y) {
        this.isOpen = false;
        this.x = x;
        this.y = y;
        this.faceTypes = new HashMap<>();
        this.sideButtons = new HashMap<>();
        this.noneSideButtons = new HashMap<>();
        this.extractionSideButtons = new HashMap<>();
        this.injectionSideButtons = new HashMap<>();
        this.client = null;
        this.sendPacketUpdate = false;
    }

    public void init(int x, int y, MinecraftClient client) {
        this.x = x;
        this.y = y;
        this.client = client;

        registerButtons(LynxMachineBlockEntity.FaceType.NONE, 90);
        registerButtons(LynxMachineBlockEntity.FaceType.EXTRACTION, 112);
        registerButtons(LynxMachineBlockEntity.FaceType.INJECTION, 134);
        for (Direction dir : LynxMachineConstants.SEARCH_DIRECTIONS) {
            this.faceTypes.put(dir, LynxMachineBlockEntity.FaceType.NONE);
            this.sideButtons.put(dir, noneSideButtons.get(dir));
        }
    }

    public void setClient(MinecraftClient client) {
        this.client = client;
    }

    private void registerButtons(LynxMachineBlockEntity.FaceType type, int u) {
        int startX = this.x + (WIDTH / 2) - (BUTTON_SIZE / 2) - 4 - BUTTON_SIZE;
        int startY = this.y + 9 + 15;

        TexturedButtonWidget north = new TexturedButtonWidget(
                startX + 18 + 4, startY, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.NORTH);
                });
        TexturedButtonWidget south = new TexturedButtonWidget(
                startX + 18 + 4, startY + 36 + 8, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.SOUTH);
                });
        TexturedButtonWidget east = new TexturedButtonWidget(
                startX + 36 + 8, startY + 18 + 4, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.EAST);
                });
        TexturedButtonWidget west = new TexturedButtonWidget(
                startX, startY + 18 + 4, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.WEST);
                });
        TexturedButtonWidget up = new TexturedButtonWidget(
                startX + 36 + 8, startY, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.UP);
                });
        TexturedButtonWidget down = new TexturedButtonWidget(
                startX + 18 + 4, startY + 18 + 4, BUTTON_SIZE, BUTTON_SIZE,
                u, 2, BUTTON_SIZE + 4, LynxScreenConstants.MACHINE_SETTINGS_TEXTURE,
                button -> {
                    this.handleSideButtonPress(Direction.DOWN);
                });

        north.setTooltip(Tooltip.of(Text.of("North")));
        south.setTooltip(Tooltip.of(Text.of("South")));
        east.setTooltip(Tooltip.of(Text.of("East")));
        west.setTooltip(Tooltip.of(Text.of("West")));
        up.setTooltip(Tooltip.of(Text.of("Up")));
        down.setTooltip(Tooltip.of(Text.of("Down")));

        if (type == LynxMachineBlockEntity.FaceType.NONE) {
            noneSideButtons.put(Direction.NORTH, north);
            noneSideButtons.put(Direction.SOUTH, south);
            noneSideButtons.put(Direction.EAST, east);
            noneSideButtons.put(Direction.WEST, west);
            noneSideButtons.put(Direction.UP, up);
            noneSideButtons.put(Direction.DOWN, down);
        }
        if (type == LynxMachineBlockEntity.FaceType.INJECTION) {
            injectionSideButtons.put(Direction.NORTH, north);
            injectionSideButtons.put(Direction.SOUTH, south);
            injectionSideButtons.put(Direction.EAST, east);
            injectionSideButtons.put(Direction.WEST, west);
            injectionSideButtons.put(Direction.UP, up);
            injectionSideButtons.put(Direction.DOWN, down);
        }
        if (type == LynxMachineBlockEntity.FaceType.EXTRACTION) {
            extractionSideButtons.put(Direction.NORTH, north);
            extractionSideButtons.put(Direction.SOUTH, south);
            extractionSideButtons.put(Direction.EAST, east);
            extractionSideButtons.put(Direction.WEST, west);
            extractionSideButtons.put(Direction.UP, up);
            extractionSideButtons.put(Direction.DOWN, down);
        }
    }

    public LynxMachineBlockEntity.FaceType rotateFaceType(LynxMachineBlockEntity.FaceType type) {
        if (type == LynxMachineBlockEntity.FaceType.EXTRACTION) return LynxMachineBlockEntity.FaceType.INJECTION;
        if (type == LynxMachineBlockEntity.FaceType.INJECTION) return LynxMachineBlockEntity.FaceType.NONE;
        return LynxMachineBlockEntity.FaceType.EXTRACTION;
    }

    public void handleSideButtonPress(Direction direction) {
        this.sendPacketUpdate = true;
        LynxMachineBlockEntity.FaceType newType = rotateFaceType(this.faceTypes.get(direction));
        this.faceTypes.put(direction, newType);
        updateSideButton(direction);
    }

    public void toggleOpen() {
        setOpen(!this.isOpen);
    }

    public void setOpen(boolean opened) {
        if (isOpen) {
            this.reset();
        }
        this.isOpen = opened;
    }

    public void reset() {

    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isOpen()) {
            return;
        }
        context.getMatrices().push();
        context.drawTexture(LynxScreenConstants.MACHINE_SETTINGS_TEXTURE, this.x, this.y, 1, 1, WIDTH, HEIGHT);
        context.drawText(this.client.textRenderer, "Config", this.x + 9, this.y + 9, 000000, false);
        for (TexturedButtonWidget button : this.sideButtons.values()) {
            button.render(context, mouseX, mouseY, delta);
        }
        context.getMatrices().pop();
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return this.isOpen ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public void update() {
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight, int button) {
        if (!this.isOpen()) {
            return true;
        }
        return mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (this.x + WIDTH) || mouseY >= (double) (this.y + HEIGHT);
    }

    public boolean isClickWithinSettingsBounds(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX <= this.x + WIDTH && mouseY <= this.y + HEIGHT;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Lynx.LOGGER.info("Click machine settings...");
        Lynx.LOGGER.info(String.format("%f, %f, %d", mouseX, mouseY, button));
        if (!this.isOpen()) {
            return false;
        }
        if (isClickWithinSettingsBounds(mouseX, mouseY)) {
            this.sideButtons.forEach((dir, sideButton) -> {
                sideButton.mouseClicked(mouseX, mouseY, 0);
            });
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void updateFaceTypes(HashMap<Direction, LynxMachineBlockEntity.FaceType> faceTypes) {
        if (shouldSendPacketUpdate()) return;

        for (Direction dir : faceTypes.keySet()) {
            this.faceTypes.put(dir, faceTypes.get(dir));
            updateSideButton(dir);
        }
    }

    public void updateSideButton(Direction direction) {
        LynxMachineBlockEntity.FaceType type = this.faceTypes.get(direction);

        if (type == LynxMachineBlockEntity.FaceType.INJECTION) {
            this.sideButtons.put(direction, this.injectionSideButtons.get(direction));
        } else if (type == LynxMachineBlockEntity.FaceType.EXTRACTION) {
            this.sideButtons.put(direction, this.extractionSideButtons.get(direction));
        } else {
            this.sideButtons.put(direction, this.noneSideButtons.get(direction));
        }
    }

    public boolean shouldSendPacketUpdate() {
        return sendPacketUpdate;
    }

    public void setSendPacketUpdate(boolean sendPacketUpdate) {
        this.sendPacketUpdate = sendPacketUpdate;
    }

    public HashMap<Direction, LynxMachineBlockEntity.FaceType> getFaceTypes() {
        return faceTypes;
    }
}
