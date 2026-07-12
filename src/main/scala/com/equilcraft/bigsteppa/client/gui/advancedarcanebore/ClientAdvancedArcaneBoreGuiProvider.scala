package com.equilcraft.bigsteppa.client.gui.advancedarcanebore

import com.equilcraft.bigsteppa.common.gui.advancedarcanebore.AdvancedArcaneBoreGuiProvider
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class ClientAdvancedArcaneBoreGuiProvider extends AdvancedArcaneBoreGuiProvider {
  override def getClientGuiElement(
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef = world.getTileEntity(x, y, z) match {
    case bore: TileAdvancedArcaneBore => new GuiAdvancedArcaneBore(player.inventory, bore)
    case _ => null
  }
}

object ClientAdvancedArcaneBoreGuiProvider {
  val instance = new ClientAdvancedArcaneBoreGuiProvider
}
