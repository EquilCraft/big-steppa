package com.equilcraft.bigsteppa.client

import com.equilcraft.bigsteppa.client.gui.armorstand.ClientArmorStandGuiProvider
import com.equilcraft.bigsteppa.client.gui.advancedarcanebore.ClientAdvancedArcaneBoreGuiProvider
import com.equilcraft.bigsteppa.client.render.armorstand.{ItemArmorStandRenderer, TileArmorStandRenderer}
import com.equilcraft.bigsteppa.common.CommonProxy
import com.equilcraft.bigsteppa.common.gui.armorstand.ArmorStandGuiProvider
import com.equilcraft.bigsteppa.common.gui.advancedarcanebore.AdvancedArcaneBoreGuiProvider
import com.equilcraft.bigsteppa.common.gui.GuiRegistry
import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBoreBase
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient
import thaumcraft.client.renderers.tile.TileArcaneBoreRenderer
import thaumcraft.client.renderers.tile.TileArcaneBoreBaseRenderer

class ClientProxy extends CommonProxy {
  override def preInit(event: FMLPreInitializationEvent): Unit = {
    super.preInit(event)
    GuiRegistry.registerClient(ArmorStandGuiProvider.armorStandGuiId, ClientArmorStandGuiProvider.instance)
    GuiRegistry.registerClient(
      AdvancedArcaneBoreGuiProvider.advancedArcaneBoreGuiId,
      ClientAdvancedArcaneBoreGuiProvider.instance
    )
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileArmorStand], new TileArmorStandRenderer)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileAdvancedArcaneBore], new TileArcaneBoreRenderer)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileAdvancedArcaneBoreBase], new TileArcaneBoreBaseRenderer)
    MinecraftForgeClient.registerItemRenderer(
      Item.getItemFromBlock(SteppaBlocks.armorStand),
      new ItemArmorStandRenderer
    )
  }
}
