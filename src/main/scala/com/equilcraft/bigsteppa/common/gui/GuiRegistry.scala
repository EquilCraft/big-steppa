package com.equilcraft.bigsteppa.common.gui

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

object GuiRegistry {
  private val providers = new Int2ObjectOpenHashMap[GuiProvider]()

  def register(id: Int, provider: GuiProvider): Unit = {
    require(!providers.containsKey(id), "GUI id is already registered: " + id)
    providers.put(id, provider)
  }

  def registerClient(id: Int, provider: GuiProvider): Unit = {
    require(providers.containsKey(id), "GUI id is not registered: " + id)
    providers.put(id, provider)
  }

  def get(id: Int): GuiProvider = providers.get(id)
}
