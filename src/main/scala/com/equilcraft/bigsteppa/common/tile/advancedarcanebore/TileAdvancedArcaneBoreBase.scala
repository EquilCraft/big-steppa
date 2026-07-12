package com.equilcraft.bigsteppa.common.tile.advancedarcanebore

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import thaumcraft.api.ThaumcraftApiHelper
import thaumcraft.api.aspects.{Aspect, IEssentiaTransport}
import thaumcraft.common.tiles.TileArcaneBoreBase

final class TileAdvancedArcaneBoreBase extends TileArcaneBoreBase {
  def drawPerditio(): Boolean = {
    var index = 0
    while (index < ForgeDirection.VALID_DIRECTIONS.length) {
      val face = ForgeDirection.VALID_DIRECTIONS(index)
      val tile = ThaumcraftApiHelper.getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, face)
      tile match {
        case transport: IEssentiaTransport
            if transport.canOutputTo(face.getOpposite) &&
              transport.getSuctionAmount(face.getOpposite) < this.getSuctionAmount(face) &&
              transport.takeEssentia(Aspect.ENTROPY, 1, face.getOpposite) == 1 =>
          return true
        case _ =>
      }
      index += 1
    }
    false
  }
}
