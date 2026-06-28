package com.equilcraft.bigsteppa.common.gui

import com.equilcraft.bigsteppa.client.gui.GuiArmorStand
import com.equilcraft.bigsteppa.common.inventory.ContainerArmorStand
import com.equilcraft.bigsteppa.common.tile.TileArmorStand
import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class ArmorStandGuiHandler extends IGuiHandler {
  override def getServerGuiElement(
    id: Int,
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef =
    world.getTileEntity(x, y, z) match {
      case armorStand: TileArmorStand if id == ArmorStandGuiHandler.GuiId =>
        new ContainerArmorStand(player.inventory, armorStand)
      case _ => null
    }

  override def getClientGuiElement(
    id: Int,
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef =
    world.getTileEntity(x, y, z) match {
      case armorStand: TileArmorStand if id == ArmorStandGuiHandler.GuiId =>
        new GuiArmorStand(player.inventory, armorStand)
      case _ => null
    }
}

object ArmorStandGuiHandler {
  final val GuiId = 1
}
