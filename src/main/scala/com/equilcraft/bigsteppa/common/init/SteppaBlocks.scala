package com.equilcraft.bigsteppa.common.init

import com.equilcraft.bigsteppa.Tags
import com.equilcraft.bigsteppa.common.block.armorstand.BlockArmorStand
import com.equilcraft.bigsteppa.common.block.beaconfarmer.{BlockBeaconFarmer, BlockMaster, BlockStructure}
import com.equilcraft.bigsteppa.common.block.alchemicalsynthesis.{
  BlockAbundanceAlchemicalSynthesisRune,
  BlockAlchemicalSynthesisAspectInput,
  BlockAlchemicalSynthesisCore,
  BlockAirAlchemicalSynthesisRune,
  BlockBasicAlchemicalSynthesisRune,
  BlockEarthAlchemicalSynthesisRune,
  BlockEntropyAlchemicalSynthesisRune,
  BlockFireAlchemicalSynthesisRune,
  BlockOrderAlchemicalSynthesisRune,
  BlockReinforcedAlchemicalSynthesisRune,
  BlockResonantAlchemicalSynthesisRune,
  BlockSpeedAlchemicalSynthesisRune,
  BlockStabilizedAlchemicalSynthesisRune,
  BlockWaterAlchemicalSynthesisRune
}
import com.equilcraft.bigsteppa.common.tile.{TileArmorStand, TileBeaconFarmer}
import com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis.{
  TileAlchemicalSynthesisAspectInput,
  TileAlchemicalSynthesisCore
}
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
  var alchemicalSynthesisCore: Block = null
  var alchemicalSynthesisAspectInput: Block = null
  var alchemicalSynthesisRune: Block = null
  var reinforcedAlchemicalSynthesisRune: Block = null
  var resonantAlchemicalSynthesisRune: Block = null
  var stabilizedAlchemicalSynthesisRune: Block = null
  var abundanceAlchemicalSynthesisRune: Block = null
  var speedAlchemicalSynthesisRune: Block = null
  var airAlchemicalSynthesisRune: Block = null
  var fireAlchemicalSynthesisRune: Block = null
  var waterAlchemicalSynthesisRune: Block = null
  var earthAlchemicalSynthesisRune: Block = null
  var orderAlchemicalSynthesisRune: Block = null
  var entropyAlchemicalSynthesisRune: Block = null

  def registerBlocks(): Unit = {
    master = this.registerBlock(classOf[BlockMaster], "blockMaster").setBlockName("blockMaster")
    structure = this.registerBlock(classOf[BlockStructure], "blockStructure").setBlockName("blockStructure")
    beaconFarmer =
      this.registerBlock(classOf[BlockBeaconFarmer], "blockBeaconFarmer").setBlockName("blockBeaconFarmer")
    armorStand = this.registerBlock(classOf[BlockArmorStand], "armorStand").setBlockName("armorStand")
    alchemicalSynthesisRune =
      this.registerBlock(new BlockBasicAlchemicalSynthesisRune, "alchemicalSynthesisRune")
        .setBlockName("alchemicalSynthesisRune")
    reinforcedAlchemicalSynthesisRune =
      this.registerBlock(new BlockReinforcedAlchemicalSynthesisRune, "reinforcedAlchemicalSynthesisRune")
        .setBlockName("reinforcedAlchemicalSynthesisRune")
    resonantAlchemicalSynthesisRune =
      this.registerBlock(new BlockResonantAlchemicalSynthesisRune, "resonantAlchemicalSynthesisRune")
        .setBlockName("resonantAlchemicalSynthesisRune")
    stabilizedAlchemicalSynthesisRune =
      this.registerBlock(new BlockStabilizedAlchemicalSynthesisRune, "stabilizedAlchemicalSynthesisRune")
        .setBlockName("stabilizedAlchemicalSynthesisRune")
    abundanceAlchemicalSynthesisRune =
      this.registerBlock(new BlockAbundanceAlchemicalSynthesisRune, "abundanceAlchemicalSynthesisRune")
        .setBlockName("abundanceAlchemicalSynthesisRune")
    speedAlchemicalSynthesisRune =
      this.registerBlock(new BlockSpeedAlchemicalSynthesisRune, "speedAlchemicalSynthesisRune")
        .setBlockName("speedAlchemicalSynthesisRune")
    airAlchemicalSynthesisRune =
      this.registerBlock(new BlockAirAlchemicalSynthesisRune, "airAlchemicalSynthesisRune")
        .setBlockName("airAlchemicalSynthesisRune")
    fireAlchemicalSynthesisRune =
      this.registerBlock(new BlockFireAlchemicalSynthesisRune, "fireAlchemicalSynthesisRune")
        .setBlockName("fireAlchemicalSynthesisRune")
    waterAlchemicalSynthesisRune =
      this.registerBlock(new BlockWaterAlchemicalSynthesisRune, "waterAlchemicalSynthesisRune")
        .setBlockName("waterAlchemicalSynthesisRune")
    earthAlchemicalSynthesisRune =
      this.registerBlock(new BlockEarthAlchemicalSynthesisRune, "earthAlchemicalSynthesisRune")
        .setBlockName("earthAlchemicalSynthesisRune")
    orderAlchemicalSynthesisRune =
      this.registerBlock(new BlockOrderAlchemicalSynthesisRune, "orderAlchemicalSynthesisRune")
        .setBlockName("orderAlchemicalSynthesisRune")
    entropyAlchemicalSynthesisRune =
      this.registerBlock(new BlockEntropyAlchemicalSynthesisRune, "entropyAlchemicalSynthesisRune")
        .setBlockName("entropyAlchemicalSynthesisRune")
    alchemicalSynthesisCore =
      this.registerBlock(new BlockAlchemicalSynthesisCore, "alchemicalSynthesisCore")
        .setBlockName("alchemicalSynthesisCore")
    alchemicalSynthesisAspectInput =
      this.registerBlock(new BlockAlchemicalSynthesisAspectInput, "alchemicalSynthesisAspectInput")
        .setBlockName("alchemicalSynthesisAspectInput")
  }

  def registerTileEntities(): Unit = {
    this.registerTileEntity(classOf[TileBeaconFarmer], "tileBeaconFarmer")
    this.registerTileEntity(classOf[TileArmorStand], "tileArmorStand")
    this.registerTileEntity(classOf[TileAlchemicalSynthesisCore], "tileAlchemicalSynthesisCore")
    this.registerTileEntity(classOf[TileAlchemicalSynthesisAspectInput], "tileAlchemicalSynthesisAspectInput")
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
    GameRegistry.registerBlock(block.newInstance.asInstanceOf[Block], blockName)

  private def registerBlock(block: Block, blockName: String): Block =
    GameRegistry.registerBlock(block, blockName)

  private def registerTileEntity(tileEntity: Class[_ <: TileEntity], teName: String): Unit = {
    GameRegistry.registerTileEntity(tileEntity, identifier + teName)
  }
}
