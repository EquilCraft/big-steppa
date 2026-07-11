package com.equilcraft.bigsteppa.common.blocks.beaconfarmer

import com.equilcraft.bigsteppa.BigSteppa
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class BlockMaster extends Block(Material.iron) {
  override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {
    super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ)

    var structureCords: List[(Int, Int, Int)] = List()

    for (ix <- -8 to 8) {
      for (iy <- -8 to 8) {
        for (iz <- -8 to 8) {
          if (worldIn.getBlock(x + ix, y + iy, z + iz).isInstanceOf[BlockStructure]) {
            structureCords = (ix, iy, iz) :: structureCords
          }
        }
      }
    }

    BigSteppa.log.info("Structure has coordinates: " + structureCords.mkString("(", ", ", ")"))

    true
  }
}
