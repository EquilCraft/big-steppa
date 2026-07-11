package com.equilcraft.bigsteppa.client.gui.armorstand

import com.equilcraft.bigsteppa.common.inventory.armorstand.ContainerArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.StatCollector
import org.lwjgl.opengl.GL11

class GuiArmorStand(
  playerInventory: InventoryPlayer,
  armorStand: TileArmorStand
) extends GuiContainer(new ContainerArmorStand(playerInventory, armorStand)) {

  xSize = 176
  ySize = 166

  override protected def drawGuiContainerForegroundLayer(
    mouseX: Int,
    mouseY: Int
  ): Unit = {
    fontRendererObj.drawString(
      StatCollector.translateToLocal("container.bigsteppa.armorStand"),
      8,
      6,
      0x404040
    )
    fontRendererObj.drawString(
      StatCollector.translateToLocal("container.inventory"),
      8,
      ySize - 96 + 2,
      0x404040
    )
  }

  override protected def drawGuiContainerBackgroundLayer(
    partialTicks: Float,
    mouseX: Int,
    mouseY: Int
  ): Unit = {
    val left = (width - xSize) / 2
    val top = (height - ySize) / 2

    GL11.glDisable(GL11.GL_TEXTURE_2D)
    drawGradientRect(
      left,
      top,
      left + xSize,
      top + ySize,
      0xFFD8D8D8L.toInt,
      0xFFC6C6C6L.toInt
    )
    drawGradientRect(
      left + 4,
      top + 4,
      left + xSize - 4,
      top + ySize - 4,
      0xFFE8E8E8L.toInt,
      0xFFD0D0D0L.toInt
    )

    for (armorType <- 0 until 4) {
      drawSlot(left + 79, top + 7 + armorType * 18)
      drawSlot(left + 125, top + 7 + armorType * 18)
    }
    for {
      row <- 0 until 3
      column <- 0 until 9
    } {
      drawSlot(left + 7 + column * 18, top + 83 + row * 18)
    }
    for (column <- 0 until 9) {
      drawSlot(left + 7 + column * 18, top + 141)
    }

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
  }

  private def drawSlot(x: Int, y: Int): Unit = {
    drawGradientRect(
      x,
      y,
      x + 18,
      y + 18,
      0xFF777777L.toInt,
      0xFFFFFFFFL.toInt
    )
    drawGradientRect(
      x + 1,
      y + 1,
      x + 17,
      y + 17,
      0xFF8B8B8BL.toInt,
      0xFF8B8B8BL.toInt
    )
  }
}
