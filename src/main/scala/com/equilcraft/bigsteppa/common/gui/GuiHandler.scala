package com.equilcraft.bigsteppa.common.gui

import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

final class GuiHandler extends IGuiHandler {
  override def getServerGuiElement(
    id: Int,
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef = {
    val provider = GuiRegistry.get(id)
    if (provider == null) null else provider.getServerGuiElement(player, world, x, y, z)
  }

  override def getClientGuiElement(
    id: Int,
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AnyRef = {
    val provider = GuiRegistry.get(id)
    if (provider == null) null else provider.getClientGuiElement(player, world, x, y, z)
  }
}
