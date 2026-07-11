package com.equilcraft.bigsteppa.common.structure.beaconfarmer

import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer
import com.gtnewhorizon.structurelib.structure.adders.IBlockAdder
import com.gtnewhorizon.structurelib.structure.{IStructureDefinition, StructureDefinition, StructureUtility}
import net.minecraft.block.Block
import vazkii.botania.common.block.ModBlocks

object BeaconFarmerStructure {
  private val upgradeBlockAdder = new IBlockAdder[TileBeaconFarmer] {
    override def apply(tile: TileBeaconFarmer, block: Block, metadata: Int): Boolean =
      tile.recordUpgrade(block)
  }

  lazy val structureDefinition: IStructureDefinition[TileBeaconFarmer] =
    StructureDefinition.builder[TileBeaconFarmer]()
      .addShape("main", StructureUtility.transpose(Array(
        Array(
          "     SSS     ",
          "   SS   SS   ",
          "  P       P  ",
          " S         S ",
          " S         S ",
          "S           S",
          "S           S",
          "S           S",
          " S         S ",
          " S         S ",
          "  P       P  ",
          "   SS   SS   ",
          "     SSS     "
        ),
        Array(
          "             ",
          "             ",
          "             ",
          "             ",
          "             ",
          "             ",
          "      ~      ",
          "             ",
          "             ",
          "             ",
          "             ",
          "             ",
          "             "
        )
      )))
      .addElement('P', StructureUtility.ofBlock[TileBeaconFarmer](ModBlocks.pylon, 2))
      .addElement(
        'S',
        StructureUtility.ofBlockAdder[TileBeaconFarmer](upgradeBlockAdder, SteppaBlocks.structure, 0)
      )
      .build()
}
