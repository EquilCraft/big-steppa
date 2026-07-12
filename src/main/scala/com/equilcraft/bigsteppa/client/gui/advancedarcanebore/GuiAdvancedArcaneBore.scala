package com.equilcraft.bigsteppa.client.gui.advancedarcanebore

import com.equilcraft.bigsteppa.common.inventory.advancedarcanebore.ContainerAdvancedArcaneBore
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

final class GuiAdvancedArcaneBore(playerInventory: InventoryPlayer, bore: TileAdvancedArcaneBore)
    extends GuiContainer(new ContainerAdvancedArcaneBore(playerInventory, bore)) {
  private final val texture = new ResourceLocation("thaumcraft", "textures/gui/gui_arcanebore.png")
  private var depthField: GuiTextField = null

  this.xSize = 176
  this.ySize = 165

  override def initGui(): Unit = {
    super.initGui()
    val left = (this.width - this.xSize) / 2
    val top = (this.height - this.ySize) / 2
    this.buttonList.add(new GuiButton(0, left + 109, top + 29, 18, 16, "-"))
    this.buttonList.add(new GuiButton(1, left + 147, top + 29, 18, 16, "+"))
    this.depthField = new GuiTextField(this.fontRendererObj, left + 140, top + 46, 30, 12)
    this.depthField.setMaxStringLength(3)
    this.depthField.setText(this.bore.excavationDepth.toString)
  }

  override protected def actionPerformed(button: GuiButton): Unit = {
    if (button.id == 0 || button.id == 1) {
      val container = this.inventorySlots.asInstanceOf[ContainerAdvancedArcaneBore]
      this.mc.playerController.sendEnchantPacket(container.windowId, button.id)
      container.enchantItem(this.mc.thePlayer, button.id)
    }
  }

  override protected def keyTyped(character: Char, keyCode: Int): Unit = {
    super.keyTyped(character, keyCode)
    if (this.depthField != null && this.depthField.textboxKeyTyped(character, keyCode)) {
      val text = this.depthField.getText
      val numeric = text.filter(digit => digit >= '0' && digit <= '9')
      if (numeric != text) {
        this.depthField.setText(numeric)
        this.depthField.setCursorPositionEnd()
      }
      this.sendDepth()
    }
  }

  override protected def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    if (this.depthField != null) {
      val wasFocused = this.depthField.isFocused
      this.depthField.mouseClicked(mouseX, mouseY, mouseButton)
      if (!wasFocused && this.depthField.isFocused && mouseButton == 0) {
        this.depthField.setCursorPositionEnd()
        this.depthField.setSelectionPos(0)
      }
    }
    this.sendDepth()
  }

  override def updateScreen(): Unit = {
    super.updateScreen()
    if (this.depthField != null && !this.depthField.isFocused) {
      this.depthField.setText(this.bore.excavationDepth.toString)
    }
  }

  override protected def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
    val container = this.inventorySlots.asInstanceOf[ContainerAdvancedArcaneBore]
    val bore = container.bore
    this.fontRendererObj.drawStringWithShadow("Width: " + bore.excavationWidth + "x" + bore.excavationWidth, 110, 8, 0xffffff)
    this.fontRendererObj.drawStringWithShadow("Perditio: " + bore.storedPerditio + "/64", 110, 19, 0xffffff)
    this.fontRendererObj.drawStringWithShadow("Depth", 110, 49, 0xffffff)
    this.fontRendererObj.drawStringWithShadow("max 128", 140, 60, 0xffffff)
    val fortuneText = if (bore.fortune > 0) " Fortune " + bore.fortune else ""
    this.fontRendererObj.drawStringWithShadow("Speed: +" + bore.speed + fortuneText, 8, 64, 0xffffff)
    if (bore.getStackInSlot(1) != null && bore.getStackInSlot(1).getItemDamage() + 1 >= bore.getStackInSlot(1).getMaxDamage) {
      this.fontRendererObj.drawStringWithShadow("Tool is nearly broken", 8, 42, 0xff5555)
    }
  }

  override protected def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    this.mc.getTextureManager.bindTexture(this.texture)
    val left = (this.width - this.xSize) / 2
    val top = (this.height - this.ySize) / 2
    this.drawTexturedModalRect(left, top, 0, 0, this.xSize, 53)
    this.drawTexturedModalRect(left, top + 77, 0, 53, this.xSize, 88)
    if (this.depthField != null) this.depthField.drawTextBox()
  }

  private def sendDepth(): Unit = {
    if (this.depthField == null) return
    val value = try this.depthField.getText.toInt catch {
      case _: NumberFormatException => return
    }
    val bounded = math.max(1, math.min(128, value))
    if (bounded != value) this.depthField.setText(bounded.toString)
    val container = this.inventorySlots.asInstanceOf[ContainerAdvancedArcaneBore]
    this.mc.playerController.sendEnchantPacket(container.windowId, 1000 + bounded)
    container.bore.setExcavationDepth(bounded)
  }
}
