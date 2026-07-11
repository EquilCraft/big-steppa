package vazkii.botania.common.entity

import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer

import java.util

object EntityDopplegangerHelper {
  def setFakePlayer(doppleganger: EntityDoppleganger): Unit =
    doppleganger.playersWhoAttacked = util.Arrays.asList(TileBeaconFarmer.fakePlayerName)

  def hasFakePlayer(doppleganger: EntityDoppleganger): Boolean =
    doppleganger.playersWhoAttacked.contains(TileBeaconFarmer.fakePlayerName)
}
