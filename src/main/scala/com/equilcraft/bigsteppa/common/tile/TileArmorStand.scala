package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry

class TileArmorStand extends com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand {

  override def validate(): Unit = {
    super.validate()
    TileArmorStand.registry.add(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
  }

  override def invalidate(): Unit = {
    super.invalidate()
    TileArmorStand.registry.remove(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
  }
}

object TileArmorStand {
  val registry = new BlocksChaosStructureRegistry[TileArmorStand]()
}
