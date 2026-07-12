package com.equilcraft.bigsteppa.client.render.armorstand

import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.EntityLiving
import net.minecraft.util.ResourceLocation

private[render] class ArmorStandBipedRenderer
  extends RenderBiped(new ModelBiped(0.0F), 0.0F) {

  this.hideModel(this.modelBipedMain)

  private def hideModel(model: ModelBiped): Unit = {
    model.bipedHead.showModel = false
    model.bipedHeadwear.showModel = false
    model.bipedBody.showModel = false
    model.bipedRightArm.showModel = false
    model.bipedLeftArm.showModel = false
    model.bipedRightLeg.showModel = false
    model.bipedLeftLeg.showModel = false
  }

  override protected def getEntityTexture(entity: EntityLiving): ResourceLocation =
    ArmorStandBipedRenderer.steveTexture
}

private object ArmorStandBipedRenderer {
  val steveTexture =
    new ResourceLocation("minecraft", "textures/entity/steve.png")
}
