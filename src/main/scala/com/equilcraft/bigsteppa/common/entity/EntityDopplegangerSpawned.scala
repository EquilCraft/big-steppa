package com.equilcraft.bigsteppa.common.entity

import com.equilcraft.bigsteppa.common.entity.EntityDopplegangerSpawned.playersAround
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.{World, WorldServer}
import vazkii.botania.common.entity.EntityDoppleganger

import java.util
import java.util.Collections

class EntityDopplegangerSpawned(val world: World) extends EntityDoppleganger(world) {
  override def getPlayersAround: util.List[EntityPlayer] = if (this.world.isRemote) Collections.emptyList() else playersAround
}

object EntityDopplegangerSpawned {
  lazy val playersAround: util.List[EntityPlayer] = Collections.singletonList(null)
}
