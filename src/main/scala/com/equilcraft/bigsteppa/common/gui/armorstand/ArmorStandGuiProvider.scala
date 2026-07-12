package com.equilcraft.bigsteppa.common.gui.armorstand

import com.equilcraft.bigsteppa.common.gui.GuiProvider
import com.equilcraft.bigsteppa.common.container.armorstand.ContainerArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class ArmorStandGuiProvider extends GuiProvider {
  override def getServerGuiElement(
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef =
    world.getTileEntity(x, y, z) match {
      case armorStand: TileArmorStand => new ContainerArmorStand(player.inventory, armorStand)
      case _ => null
    }
}

object ArmorStandGuiProvider {
  final val armorStandGuiId = 1
  val instance = new ArmorStandGuiProvider
}
