package com.equilcraft.bigsteppa.common

import com.equilcraft.bigsteppa.Config
import com.equilcraft.bigsteppa.common.init.{SteppaBlocks, SteppaEntities}
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {
    Config.synchronizeConfiguration(event.getSuggestedConfigurationFile)

    SteppaBlocks.registerBlocks()
    SteppaBlocks.registerTileEntities()
  }

  def init(event: FMLInitializationEvent): Unit = {
    SteppaEntities.registerEntities()
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
  }

  def serverStarting(event: FMLServerStartingEvent): Unit = {
  }
}
