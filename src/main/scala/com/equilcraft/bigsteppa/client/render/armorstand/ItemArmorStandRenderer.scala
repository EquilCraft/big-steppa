package com.equilcraft.bigsteppa.client.render.armorstand

import com.equilcraft.bigsteppa.client.model.armorstand.ModelArmorStand
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRenderType, ItemRendererHelper}
import org.lwjgl.opengl.{GL11, GL12}

class ItemArmorStandRenderer extends IItemRenderer {
  private val model = new ModelArmorStand

  override def handleRenderType(item: ItemStack, renderType: ItemRenderType): Boolean =
    true

  override def shouldUseRenderHelper(
    renderType: ItemRenderType,
    item: ItemStack,
    helper: ItemRendererHelper
  ): Boolean =
    renderType == ItemRenderType.ENTITY

  override def renderItem(
    renderType: ItemRenderType,
    item: ItemStack,
    data: AnyRef*
  ): Unit = {
    GL11.glPushMatrix()
    val scale = renderType match {
      case ItemRenderType.ENTITY =>
        GL11.glTranslatef(0.0F, 0.68F, 0.0F)
        0.45F
      case ItemRenderType.INVENTORY =>
        GL11.glTranslatef(0.0F, 1.08F, 0.0F)
        GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F)
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F)
        0.72F
      case ItemRenderType.EQUIPPED_FIRST_PERSON =>
        GL11.glTranslatef(0.7F, 1.25F, 0.65F)
        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F)
        0.65F
      case _ =>
        GL11.glTranslatef(0.55F, 1.05F, 0.55F)
        GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F)
        0.65F
    }

    GL11.glScalef(scale, -scale, -scale)
    GL11.glEnable(GL12.GL_RESCALE_NORMAL)
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glColor4f(0.48F, 0.29F, 0.13F, 1.0F)
    model.render(0.0625F)
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glDisable(GL12.GL_RESCALE_NORMAL)
    GL11.glPopMatrix()
  }
}
