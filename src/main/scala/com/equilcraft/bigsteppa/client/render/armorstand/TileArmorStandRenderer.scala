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

  armorRenderer.setRenderManager(RenderManager.instance)

  override def renderTileEntityAt(
    tile: TileEntity,
    x: Double,
    y: Double,
    z: Double,
    partialTicks: Float
  ): Unit = tile match {
    case armorStand: TileArmorStand =>
      val yaw = (tile.getBlockMetadata & 3) * 90.0F
      renderStand(x, y, z, yaw)
      renderArmor(armorStand, x, y, z, yaw, partialTicks)
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
    standModel.render(0.0625F)
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
    ensureDummy(armorStand.getWorldObj)
    armorDummy.setCurrentItemOrArmor(4, armorStand.getStackInSlot(SlotHead))
    armorDummy.setCurrentItemOrArmor(3, armorStand.getStackInSlot(SlotChest))
    armorDummy.setCurrentItemOrArmor(2, armorStand.getStackInSlot(SlotLegs))
    armorDummy.setCurrentItemOrArmor(1, armorStand.getStackInSlot(SlotFeet))
    armorDummy.setPosition(
      armorStand.xCoord + 0.5D,
      armorStand.yCoord,
      armorStand.zCoord + 0.5D
    )
    armorDummy.prevRenderYawOffset = yaw
    armorDummy.renderYawOffset = yaw
    armorDummy.prevRotationYawHead = yaw
    armorDummy.rotationYawHead = yaw
    armorDummy.prevRotationYaw = yaw
    armorDummy.rotationYaw = yaw
    armorDummy.prevRotationPitch = 0.0F
    armorDummy.rotationPitch = 0.0F
    armorDummy.limbSwing = 0.0F
    armorDummy.limbSwingAmount = 0.0F
    armorDummy.prevLimbSwingAmount = 0.0F

    GL11.glPushMatrix()
    armorRenderer.doRender(
      armorDummy,
      x + 0.5D,
      y,
      z + 0.5D,
      yaw,
      partialTicks
    )
    GL11.glPopMatrix()
  }

  private def ensureDummy(world: World): Unit = {
    if (armorDummy == null || dummyWorld != world) {
      dummyWorld = world
      armorDummy = new EntityLiving(world) {}
    }
  }
}
