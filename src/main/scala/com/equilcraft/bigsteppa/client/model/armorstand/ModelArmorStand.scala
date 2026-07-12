package com.equilcraft.bigsteppa.client.model.armorstand

import net.minecraft.client.model.{ModelBase, ModelRenderer}

class ModelArmorStand extends ModelBase {
  this.textureWidth = 64
  this.textureHeight = 32

  private val base =
    part(-7.0F, -2.0F, -5.0F, 14, 2, 10, 0.0F, 24.0F, 0.0F)
  private val upright =
    part(-1.0F, -20.0F, -1.0F, 2, 20, 2, 0.0F, 22.0F, 0.0F)
  private val shoulders =
    part(-7.0F, -1.0F, -1.5F, 14, 2, 3, 0.0F, 4.0F, 0.0F)
  private val neck =
    part(-2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, 2.0F, 0.0F)
  private val leftBrace =
    part(-1.0F, 0.0F, -1.0F, 2, 8, 2, -5.5F, 4.0F, 0.0F)
  private val rightBrace =
    part(-1.0F, 0.0F, -1.0F, 2, 8, 2, 5.5F, 4.0F, 0.0F)

  this.leftBrace.rotateAngleZ = -0.62F
  this.rightBrace.rotateAngleZ = 0.62F

  private def part(
    boxX: Float,
    boxY: Float,
    boxZ: Float,
    width: Int,
    height: Int,
    depth: Int,
    pointX: Float,
    pointY: Float,
    pointZ: Float
  ): ModelRenderer = {
    val renderer = new ModelRenderer(this, 0, 0)
    renderer.addBox(boxX, boxY, boxZ, width, height, depth)
    renderer.setRotationPoint(pointX, pointY, pointZ)
    renderer
  }

  def render(scale: Float): Unit = {
    this.base.render(scale)
    this.upright.render(scale)
    this.shoulders.render(scale)
    this.neck.render(scale)
    this.leftBrace.render(scale)
    this.rightBrace.render(scale)
  }
}
