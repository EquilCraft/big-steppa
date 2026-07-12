package com.equilcraft.bigsteppa.common

import com.equilcraft.bigsteppa.{BigSteppa, Config}
import com.equilcraft.bigsteppa.common.init.AlchemicalSynthesisRegistry
import com.equilcraft.bigsteppa.common.gui.armorstand.ArmorStandGuiProvider
import com.equilcraft.bigsteppa.common.gui.advancedarcanebore.AdvancedArcaneBoreGuiProvider
import com.equilcraft.bigsteppa.common.gui.{GuiHandler, GuiRegistry}
import com.equilcraft.bigsteppa.common.init.{SteppaBlocks, SteppaEntities}
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import cpw.mods.fml.common.network.NetworkRegistry

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {
    Config.synchronizeConfiguration(event.getSuggestedConfigurationFile)

    SteppaBlocks.registerBlocks()
    SteppaBlocks.registerTileEntities()
    SteppaBlocks.registerRecipes()
    GuiRegistry.register(ArmorStandGuiProvider.armorStandGuiId, ArmorStandGuiProvider.instance)
    GuiRegistry.register(AdvancedArcaneBoreGuiProvider.advancedArcaneBoreGuiId, AdvancedArcaneBoreGuiProvider.instance)
    NetworkRegistry.INSTANCE.registerGuiHandler(BigSteppa, new GuiHandler)
  }

  def init(event: FMLInitializationEvent): Unit = {
    SteppaEntities.registerEntities()
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
    AlchemicalSynthesisRegistry.initialize()
  }

  def serverStarting(event: FMLServerStartingEvent): Unit = {
  }
}
