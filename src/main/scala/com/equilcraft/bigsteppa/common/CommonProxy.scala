package com.equilcraft.bigsteppa.common

import com.equilcraft.bigsteppa.{BigSteppa, Config}
import com.equilcraft.bigsteppa.common.gui.ArmorStandGuiHandler
import com.equilcraft.bigsteppa.common.init.{SteppaBlocks, SteppaEntities}
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import cpw.mods.fml.common.network.NetworkRegistry

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {
    Config.synchronizeConfiguration(event.getSuggestedConfigurationFile)

    SteppaBlocks.registerBlocks()
    SteppaBlocks.registerTileEntities()
    SteppaBlocks.registerRecipes()
    NetworkRegistry.INSTANCE.registerGuiHandler(BigSteppa, new ArmorStandGuiHandler)
  }

  def init(event: FMLInitializationEvent): Unit = {
    SteppaEntities.registerEntities()
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
  }

  def serverStarting(event: FMLServerStartingEvent): Unit = {
  }
}
