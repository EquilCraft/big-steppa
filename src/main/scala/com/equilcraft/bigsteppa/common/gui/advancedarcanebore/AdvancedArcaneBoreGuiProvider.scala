package com.equilcraft.bigsteppa.common.gui.advancedarcanebore

import com.equilcraft.bigsteppa.common.gui.GuiProvider
import com.equilcraft.bigsteppa.common.container.advancedarcanebore.ContainerAdvancedArcaneBore
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class AdvancedArcaneBoreGuiProvider extends GuiProvider {
  override def getServerGuiElement(
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef = world.getTileEntity(x, y, z) match {
    case bore: TileAdvancedArcaneBore => new ContainerAdvancedArcaneBore(player.inventory, bore)
    case _ => null
  }
}

object AdvancedArcaneBoreGuiProvider {
  final val advancedArcaneBoreGuiId = 2
  val instance = new AdvancedArcaneBoreGuiProvider
}
