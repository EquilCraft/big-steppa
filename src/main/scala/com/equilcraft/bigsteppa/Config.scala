package com.equilcraft.bigsteppa

import net.minecraftforge.common.config.Configuration

import java.io.File

object Config {
  var greeting = "Hello World"

  def synchronizeConfiguration(configFile: File): Unit = {
    val configuration = new Configuration(configFile)
    greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?")
    if (configuration.hasChanged) configuration.save()
  }
}
