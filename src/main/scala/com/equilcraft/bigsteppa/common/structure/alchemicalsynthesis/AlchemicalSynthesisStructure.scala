package com.equilcraft.bigsteppa.common.structure.alchemicalsynthesis

import com.equilcraft.bigsteppa.common.blocks.alchemicalsynthesis.BlockAlchemicalSynthesisRune
import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis.TileAlchemicalSynthesisCore
import com.gtnewhorizon.structurelib.structure.adders.IBlockAdder
import com.gtnewhorizon.structurelib.structure.{IStructureDefinition, StructureDefinition, StructureUtility}
import net.minecraft.block.Block

object AlchemicalSynthesisStructure {
  private val runeAdder = new IBlockAdder[TileAlchemicalSynthesisCore] {
    override def apply(core: TileAlchemicalSynthesisCore, block: Block, metadata: Int): Boolean = {
      if (metadata != 0 || !block.isInstanceOf[BlockAlchemicalSynthesisRune]) return false

      core.recordRune(block.asInstanceOf[BlockAlchemicalSynthesisRune].runeId)
      true
    }
  }

  private def createShape(): Array[Array[String]] = {
    val aspectInputLayer = Array(
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "     I     ",
      "           ",
      "           ",
      "           ",
      "           ",
      "           "
    )
    val topLayer = Array(
      "RRRRRRRRRRR",
      "R    R    R",
      "R    R    R",
      "R    R    R",
      "R    R    R",
      "RRRRR~RRRRR",
      "R    R    R",
      "R    R    R",
      "R    R    R",
      "R    R    R",
      "RRRRRRRRRRR"
    )
    val runeLayer = Array(
      "R         R",
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "           ",
      "R         R"
    )
    val layers = Array(aspectInputLayer, topLayer) ++ Array.fill(6)(runeLayer)
    require(layers.iterator.flatMap(_.iterator).map(_.count(_ == 'R')).sum == 80)
    StructureUtility.transpose(layers)
  }

  lazy val structureDefinition: IStructureDefinition[TileAlchemicalSynthesisCore] =
    StructureDefinition
      .builder[TileAlchemicalSynthesisCore]()
      .addShape("main", this.createShape())
      .addElement(
        'R',
        StructureUtility.ofBlockAdder[TileAlchemicalSynthesisCore](
          this.runeAdder,
          SteppaBlocks.alchemicalSynthesisRune,
          0
        )
      )
      .addElement(
        'I',
        StructureUtility.ofBlock[TileAlchemicalSynthesisCore](SteppaBlocks.alchemicalSynthesisAspectInput, 0)
      )
      .build()
}
