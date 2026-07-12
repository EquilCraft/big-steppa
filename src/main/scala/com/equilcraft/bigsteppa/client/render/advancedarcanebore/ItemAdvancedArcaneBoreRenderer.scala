package com.equilcraft.bigsteppa.client.render.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.{TileAdvancedArcaneBore, TileAdvancedArcaneBoreBase}
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRenderType, ItemRendererHelper}
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX
import thaumcraft.client.renderers.tile.{TileArcaneBoreBaseRenderer, TileArcaneBoreRenderer}

final class ItemAdvancedArcaneBoreRenderer(base: Boolean) extends IItemRenderer {
  private val boreRenderer = new TileArcaneBoreRenderer
  private val baseRenderer = new TileArcaneBoreBaseRenderer

  override def handleRenderType(item: ItemStack, renderType: ItemRenderType): Boolean = true

  override def shouldUseRenderHelper(
    renderType: ItemRenderType,
    item: ItemStack,
    helper: ItemRendererHelper
  ): Boolean = true

  override def renderItem(
    renderType: ItemRenderType,
    item: ItemStack,
    data: AnyRef*
  ): Unit = {
    GL11.glPushMatrix()
    if (renderType == ItemRenderType.EQUIPPED || renderType == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glTranslatef(1.0F, 1.0F, 1.0F)
    }
    GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F)
    if (this.base) GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
    else GL11.glTranslatef(-0.5F, -0.75F, -0.5F)
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    UtilsFX.bindTexture("textures/models/Bore.png")
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    if (this.base) {
      this.baseRenderer.renderEntityAt(
        new TileAdvancedArcaneBoreBase,
        0.0D,
        0.0D,
        0.0D,
        0.0F
      )
    } else {
      this.boreRenderer.renderEntityAt(
        new TileAdvancedArcaneBore,
        0.0D,
        0.0D,
        0.0D,
        0.0F
      )
    }
    GL11.glPopMatrix()
  }
}
