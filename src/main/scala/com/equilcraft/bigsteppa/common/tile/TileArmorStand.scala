package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry

class TileArmorStand extends com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand {

  override def validate(): Unit = {
    super.validate()
    TileArmorStand.registry.add(worldObj, xCoord, yCoord, zCoord)
  }

  override def invalidate(): Unit = {
    super.invalidate()
    TileArmorStand.registry.remove(worldObj, xCoord, yCoord, zCoord)
  }
}

object TileArmorStand {
  val registry = new BlocksChaosStructureRegistry[TileArmorStand]()
}
