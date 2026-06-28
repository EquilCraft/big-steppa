package com.equilcraft.bigsteppa.client.gui;

import com.equilcraft.bigsteppa.common.inventory.ContainerArmorStand;
import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiArmorStand extends GuiContainer {

    public GuiArmorStand(InventoryPlayer playerInventory, TileArmorStand armorStand) {
        super(new ContainerArmorStand(playerInventory, armorStand));
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(
            StatCollector.translateToLocal("container.bigsteppa.armorStand"),
            8,
            6,
            0x404040
        );
        fontRendererObj.drawString(
            StatCollector.translateToLocal("container.inventory"),
            8,
            ySize - 96 + 2,
            0x404040
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(
        float partialTicks,
        int mouseX,
        int mouseY
    ) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawGradientRect(left, top, left + xSize, top + ySize, 0xFFD8D8D8, 0xFFC6C6C6);
        drawGradientRect(left + 4, top + 4, left + xSize - 4, top + ySize - 4, 0xFFE8E8E8, 0xFFD0D0D0);

        for (int armorType = 0; armorType < 4; armorType++) {
            drawSlot(left + 79, top + 7 + armorType * 18);
            drawSlot(left + 125, top + 7 + armorType * 18);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(left + 7 + column * 18, top + 83 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(left + 7 + column * 18, top + 141);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawSlot(int x, int y) {
        drawGradientRect(x, y, x + 18, y + 18, 0xFF777777, 0xFFFFFFFF);
        drawGradientRect(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B, 0xFF8B8B8B);
    }
}
