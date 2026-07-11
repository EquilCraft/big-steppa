package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry

class TileBeaconFarmer extends com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer {

  override def validate(): Unit = {
    super.validate()
    TileBeaconFarmer.registry.add(worldObj, xCoord, yCoord, zCoord)
  }

  override def invalidate(): Unit = {
    super.invalidate()
    TileBeaconFarmer.registry.remove(worldObj, xCoord, yCoord, zCoord)
  }
}

object TileBeaconFarmer {
  val registry = new BlocksChaosStructureRegistry[TileBeaconFarmer]()
  val fakePlayerName = com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer.fakePlayerName
}
