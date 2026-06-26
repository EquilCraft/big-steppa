package com.equilcraft.bigsteppa.common.init

import com.equilcraft.bigsteppa.BigSteppa
import com.equilcraft.bigsteppa.common.entity.EntityDopplegangerSpawned
import cpw.mods.fml.common.registry.EntityRegistry
import vazkii.botania.common.Botania
import vazkii.botania.common.lib.LibEntityNames

object SteppaEntities {
  def registerEntities(): Unit = {
    EntityRegistry.registerModEntity(classOf[EntityDopplegangerSpawned], LibEntityNames.DOPPLEGANGER, 0, BigSteppa, 128, 3, true)
  }
}
