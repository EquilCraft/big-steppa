package com.equilcraft.bigsteppa.client.gui.armorstand

import com.equilcraft.bigsteppa.common.gui.armorstand.ArmorStandGuiProvider
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class ClientArmorStandGuiProvider extends ArmorStandGuiProvider {
  override def getClientGuiElement(
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef =
    world.getTileEntity(x, y, z) match {
      case armorStand: TileArmorStand => new GuiArmorStand(player.inventory, armorStand)
      case _ => null
    }
}

object ClientArmorStandGuiProvider {
  val instance = new ClientArmorStandGuiProvider
}
