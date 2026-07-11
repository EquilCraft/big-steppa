package com.equilcraft.bigsteppa.client

import com.equilcraft.bigsteppa.client.render.armorstand.{ItemArmorStandRenderer, TileArmorStandRenderer}
import com.equilcraft.bigsteppa.common.CommonProxy
import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import com.equilcraft.bigsteppa.common.tile.TileArmorStand
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient

class ClientProxy extends CommonProxy {
  override def preInit(event: FMLPreInitializationEvent): Unit = {
    super.preInit(event)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileArmorStand], new TileArmorStandRenderer)
    MinecraftForgeClient.registerItemRenderer(
      Item.getItemFromBlock(SteppaBlocks.armorStand),
      new ItemArmorStandRenderer
    )
  }
}
