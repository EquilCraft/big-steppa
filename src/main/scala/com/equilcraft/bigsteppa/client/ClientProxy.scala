package com.equilcraft.bigsteppa.client

import com.equilcraft.bigsteppa.client.gui.armorstand.ClientArmorStandGuiProvider
import com.equilcraft.bigsteppa.client.render.armorstand.{ItemArmorStandRenderer, TileArmorStandRenderer}
import com.equilcraft.bigsteppa.common.CommonProxy
import com.equilcraft.bigsteppa.common.gui.armorstand.ArmorStandGuiProvider
import com.equilcraft.bigsteppa.common.gui.GuiRegistry
import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import com.equilcraft.bigsteppa.common.tile.TileArmorStand
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient

class ClientProxy extends CommonProxy {
  override def preInit(event: FMLPreInitializationEvent): Unit = {
    super.preInit(event)
    GuiRegistry.registerClient(ArmorStandGuiProvider.armorStandGuiId, ClientArmorStandGuiProvider.instance)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileArmorStand], new TileArmorStandRenderer)
    MinecraftForgeClient.registerItemRenderer(
      Item.getItemFromBlock(SteppaBlocks.armorStand),
      new ItemArmorStandRenderer
    )
  }
}
