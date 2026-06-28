package com.equilcraft.bigsteppa.common.init

import com.equilcraft.bigsteppa.Tags
import com.equilcraft.bigsteppa.common.block.{BlockArmorStand, BlockBeaconFarmer}
import com.equilcraft.bigsteppa.common.block.build.{BlockMaster, BlockStructure}
import com.equilcraft.bigsteppa.common.tile.{TileArmorStand, TileBeaconFarmer}
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity

object SteppaBlocks {
  private final val identifier = Tags.MOD_ID + ":"

  var master: Block = null
  var structure: Block = null
  var beaconFarmer: Block = null
  var armorStand: Block = null

  def registerBlocks(): Unit = {
    master = registerBlock(classOf[BlockMaster], "blockMaster").setBlockName("blockMaster")
    structure = registerBlock(classOf[BlockStructure], "blockStructure").setBlockName("blockStructure")
    beaconFarmer = registerBlock(classOf[BlockBeaconFarmer], "blockBeaconFarmer").setBlockName("blockBeaconFarmer")
    armorStand = registerBlock(classOf[BlockArmorStand], "armorStand").setBlockName("armorStand")
  }

  def registerTileEntities(): Unit = {
    registerTileEntity(classOf[TileBeaconFarmer], "tileBeaconFarmer")
    registerTileEntity(classOf[TileArmorStand], "tileArmorStand")
  }

  def registerRecipes(): Unit = {
    GameRegistry.addRecipe(
      new ItemStack(armorStand),
      " I ",
      " I ",
      "SSS",
      Character.valueOf('I'),
      Items.iron_ingot,
      Character.valueOf('S'),
      new ItemStack(Blocks.stone_slab, 1, 0)
    )
  }

  private def registerBlock(block: Class[_], blockName: String): Block =
    GameRegistry.registerBlock(block.newInstance.asInstanceOf[Block], identifier + blockName)

  private def registerTileEntity(tileEntity: Class[_ <: TileEntity], teName: String): Unit = {
    GameRegistry.registerTileEntity(tileEntity, identifier + teName)
  }
}
