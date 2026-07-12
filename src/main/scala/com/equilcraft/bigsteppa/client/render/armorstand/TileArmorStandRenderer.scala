package com.equilcraft.bigsteppa.client.render.armorstand

import com.equilcraft.bigsteppa.client.model.armorstand.ModelArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand._
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.EntityLiving
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import org.lwjgl.opengl.{GL11, GL12}

class TileArmorStandRenderer extends TileEntitySpecialRenderer {
  private val standModel = new ModelArmorStand
  private val armorRenderer = new ArmorStandBipedRenderer
  private var armorDummy: EntityLiving = _
  private var dummyWorld: World = _

  this.armorRenderer.setRenderManager(RenderManager.instance)

  override def renderTileEntityAt(
    tile: TileEntity,
    x: Double,
    y: Double,
    z: Double,
    partialTicks: Float
  ): Unit = tile match {
    case armorStand: TileArmorStand =>
      val yaw = (tile.getBlockMetadata & 3) * 90.0F
      this.renderStand(x, y, z, yaw)
      this.renderArmor(armorStand, x, y, z, yaw, partialTicks)
    case _ =>
  }

  private def renderStand(x: Double, y: Double, z: Double, yaw: Float): Unit = {
    GL11.glPushMatrix()
    GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D)
    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F)
    GL11.glScalef(1.0F, -1.0F, -1.0F)
    GL11.glEnable(GL12.GL_RESCALE_NORMAL)
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glColor4f(0.48F, 0.29F, 0.13F, 1.0F)
    this.standModel.render(0.0625F)
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glDisable(GL12.GL_RESCALE_NORMAL)
    GL11.glPopMatrix()
  }

  private def renderArmor(
    armorStand: TileArmorStand,
    x: Double,
    y: Double,
    z: Double,
    yaw: Float,
    partialTicks: Float
  ): Unit = {
    this.ensureDummy(armorStand.getWorldObj)
    this.armorDummy.setCurrentItemOrArmor(4, armorStand.getStackInSlot(slotHead))
    this.armorDummy.setCurrentItemOrArmor(3, armorStand.getStackInSlot(slotChest))
    this.armorDummy.setCurrentItemOrArmor(2, armorStand.getStackInSlot(slotLegs))
    this.armorDummy.setCurrentItemOrArmor(1, armorStand.getStackInSlot(slotFeet))
    this.armorDummy.setPosition(
      armorStand.xCoord + 0.5D,
      armorStand.yCoord,
      armorStand.zCoord + 0.5D
    )
    this.armorDummy.prevRenderYawOffset = yaw
    this.armorDummy.renderYawOffset = yaw
    this.armorDummy.prevRotationYawHead = yaw
    this.armorDummy.rotationYawHead = yaw
    this.armorDummy.prevRotationYaw = yaw
    this.armorDummy.rotationYaw = yaw
    this.armorDummy.prevRotationPitch = 0.0F
    this.armorDummy.rotationPitch = 0.0F
    this.armorDummy.limbSwing = 0.0F
    this.armorDummy.limbSwingAmount = 0.0F
    this.armorDummy.prevLimbSwingAmount = 0.0F

    GL11.glPushMatrix()
    this.armorRenderer.doRender(
      this.armorDummy,
      x + 0.5D,
      y,
      z + 0.5D,
      yaw,
      partialTicks
    )
    GL11.glPopMatrix()
  }

  private def ensureDummy(world: World): Unit = {
    if (this.armorDummy == null || this.dummyWorld != world) {
      this.dummyWorld = world
      this.armorDummy = new EntityLiving(world) {}
    }
  }
}
