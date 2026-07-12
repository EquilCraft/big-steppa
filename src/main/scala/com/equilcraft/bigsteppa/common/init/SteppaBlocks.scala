package com.equilcraft.bigsteppa.common.init

import com.equilcraft.bigsteppa.Tags
import com.equilcraft.bigsteppa.common.block.advancedarcanebore.{BlockAdvancedArcaneBore, BlockAdvancedArcaneBoreBase, ItemBlockAdvancedArcaneBore, ItemBlockAdvancedArcaneBoreBase}
import com.equilcraft.bigsteppa.common.block.armorstand.BlockArmorStand
import com.equilcraft.bigsteppa.common.block.beaconfarmer.BlockBeaconFarmer
import com.equilcraft.bigsteppa.common.block.build.{BlockMaster, BlockStructure}
import com.equilcraft.bigsteppa.common.block.alchemicalsynthesis.{BlockAbundanceAlchemicalSynthesisRune, BlockAirAlchemicalSynthesisRune, BlockAlchemicalSynthesisAspectInput, BlockAlchemicalSynthesisCore, BlockBasicAlchemicalSynthesisRune, BlockEarthAlchemicalSynthesisRune, BlockEntropyAlchemicalSynthesisRune, BlockFireAlchemicalSynthesisRune, BlockOrderAlchemicalSynthesisRune, BlockReinforcedAlchemicalSynthesisRune, BlockResonantAlchemicalSynthesisRune, BlockSpeedAlchemicalSynthesisRune, BlockStabilizedAlchemicalSynthesisRune, BlockWaterAlchemicalSynthesisRune}
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBoreBase
import com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis.{TileAlchemicalSynthesisAspectInput, TileAlchemicalSynthesisCore}
import thaumcraft.common.config.ConfigBlocks
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
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
  var advancedArcaneBore: Block = null
  var advancedArcaneBoreBase: Block = null

  def registerBlocks(): Unit = {
    this.master = this.registerBlock(classOf[BlockMaster], "blockMaster").setBlockName("blockMaster")
    this.structure = this.registerBlock(classOf[BlockStructure], "blockStructure").setBlockName("blockStructure")
    this.beaconFarmer =
      this.registerBlock(classOf[BlockBeaconFarmer], "blockBeaconFarmer").setBlockName("blockBeaconFarmer")
    this.armorStand = this.registerBlock(classOf[BlockArmorStand], "blockArmorStand").setBlockName("blockArmorStand")
    this.alchemicalSynthesisRune =
      this.registerBlock(new BlockBasicAlchemicalSynthesisRune, "blockAlchemicalSynthesisRune")
        .setBlockName("blockAlchemicalSynthesisRune")
    this.reinforcedAlchemicalSynthesisRune =
      this.registerBlock(new BlockReinforcedAlchemicalSynthesisRune, "blockReinforcedAlchemicalSynthesisRune")
        .setBlockName("blockReinforcedAlchemicalSynthesisRune")
    this.resonantAlchemicalSynthesisRune =
      this.registerBlock(new BlockResonantAlchemicalSynthesisRune, "blockResonantAlchemicalSynthesisRune")
        .setBlockName("blockResonantAlchemicalSynthesisRune")
    this.stabilizedAlchemicalSynthesisRune =
      this.registerBlock(new BlockStabilizedAlchemicalSynthesisRune, "blockStabilizedAlchemicalSynthesisRune")
        .setBlockName("blockStabilizedAlchemicalSynthesisRune")
    this.abundanceAlchemicalSynthesisRune =
      this.registerBlock(new BlockAbundanceAlchemicalSynthesisRune, "blockAbundanceAlchemicalSynthesisRune")
        .setBlockName("blockAbundanceAlchemicalSynthesisRune")
    this.speedAlchemicalSynthesisRune =
      this.registerBlock(new BlockSpeedAlchemicalSynthesisRune, "blockSpeedAlchemicalSynthesisRune")
        .setBlockName("blockSpeedAlchemicalSynthesisRune")
    this.airAlchemicalSynthesisRune =
      this.registerBlock(new BlockAirAlchemicalSynthesisRune, "blockAirAlchemicalSynthesisRune")
        .setBlockName("blockAirAlchemicalSynthesisRune")
    this.fireAlchemicalSynthesisRune =
      this.registerBlock(new BlockFireAlchemicalSynthesisRune, "blockFireAlchemicalSynthesisRune")
        .setBlockName("blockFireAlchemicalSynthesisRune")
    this.waterAlchemicalSynthesisRune =
      this.registerBlock(new BlockWaterAlchemicalSynthesisRune, "blockWaterAlchemicalSynthesisRune")
        .setBlockName("blockWaterAlchemicalSynthesisRune")
    this.earthAlchemicalSynthesisRune =
      this.registerBlock(new BlockEarthAlchemicalSynthesisRune, "blockEarthAlchemicalSynthesisRune")
        .setBlockName("blockEarthAlchemicalSynthesisRune")
    this.orderAlchemicalSynthesisRune =
      this.registerBlock(new BlockOrderAlchemicalSynthesisRune, "blockOrderAlchemicalSynthesisRune")
        .setBlockName("blockOrderAlchemicalSynthesisRune")
    this.entropyAlchemicalSynthesisRune =
      this.registerBlock(new BlockEntropyAlchemicalSynthesisRune, "blockEntropyAlchemicalSynthesisRune")
        .setBlockName("blockEntropyAlchemicalSynthesisRune")
    this.advancedArcaneBoreBase = GameRegistry
      .registerBlock(new BlockAdvancedArcaneBoreBase, classOf[ItemBlockAdvancedArcaneBoreBase], "blockAdvancedArcaneBoreBase")
      .setBlockName("blockAdvancedArcaneBoreBase")
    this.advancedArcaneBore = GameRegistry
      .registerBlock(new BlockAdvancedArcaneBore, classOf[ItemBlockAdvancedArcaneBore], "blockAdvancedArcaneBore")
      .setBlockName("blockAdvancedArcaneBore")
    this.alchemicalSynthesisCore =
      this.registerBlock(new BlockAlchemicalSynthesisCore, "blockAlchemicalSynthesisCore")
        .setBlockName("blockAlchemicalSynthesisCore")
    this.alchemicalSynthesisAspectInput =
      this.registerBlock(new BlockAlchemicalSynthesisAspectInput, "blockAlchemicalSynthesisAspectInput")
        .setBlockName("blockAlchemicalSynthesisAspectInput")
  }

  def registerTileEntities(): Unit = {
    this.registerTileEntity(classOf[TileBeaconFarmer], "tileBeaconFarmer")
    this.registerTileEntity(classOf[TileArmorStand], "tileArmorStand")
    this.registerTileEntity(classOf[TileAlchemicalSynthesisCore], "tileAlchemicalSynthesisCore")
    this.registerTileEntity(classOf[TileAlchemicalSynthesisAspectInput], "tileAlchemicalSynthesisAspectInput")
    this.registerTileEntity(classOf[TileAdvancedArcaneBore], "tileAdvancedArcaneBore")
    this.registerTileEntity(classOf[TileAdvancedArcaneBoreBase], "tileAdvancedArcaneBoreBase")
  }

  private def registerBlock(block: Class[_], blockName: String): Block =
    GameRegistry.registerBlock(block.newInstance.asInstanceOf[Block], blockName)

  private def registerBlock(block: Block, blockName: String): Block =
    GameRegistry.registerBlock(block, blockName)

  private def registerBlock(block: Block, itemBlock: Class[_ <: ItemBlock], blockName: String): Block =
    GameRegistry.registerBlock(block, itemBlock, blockName)

  private def registerTileEntity(tileEntity: Class[_ <: TileEntity], teName: String): Unit = {
    GameRegistry.registerTileEntity(tileEntity, identifier + teName)
  }
}
