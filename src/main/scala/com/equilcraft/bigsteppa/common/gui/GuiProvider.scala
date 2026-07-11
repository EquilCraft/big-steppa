package com.equilcraft.bigsteppa.common.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

trait GuiProvider {
  def getServerGuiElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef

  def getClientGuiElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null
}
