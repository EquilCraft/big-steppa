package com.equilcraft.bigsteppa

import com.equilcraft.bigsteppa.common.CommonProxy
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import cpw.mods.fml.common.{Mod, SidedProxy}
import org.apache.logging.log4j.{LogManager, Logger}

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
  modLanguage = "scala", acceptedMinecraftVersions = "[1.7.10]",
  dependencies = "required-after:structurelib;required-after:Thaumcraft;required-after:Botania")
object BigSteppa {
  val LOG: Logger = LogManager.getLogger(Tags.MOD_ID)

  @SidedProxy(clientSide = "com.equilcraft.bigsteppa.client.ClientProxy", serverSide = "com.equilcraft.bigsteppa.common.CommonProxy")
  var proxy: CommonProxy = null

  @Mod.EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    proxy.preInit(event)
  }

  @Mod.EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    proxy.init(event)
  }

  @Mod.EventHandler
  def postInit(event: FMLPostInitializationEvent): Unit = {
    proxy.postInit(event)
  }

  @Mod.EventHandler
  def serverStarting(event: FMLServerStartingEvent): Unit = {
    proxy.serverStarting(event)
  }
}
