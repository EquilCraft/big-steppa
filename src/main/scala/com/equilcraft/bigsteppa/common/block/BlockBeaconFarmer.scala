package com.equilcraft.bigsteppa.common.block

import com.equilcraft.bigsteppa.common.tile.TileBeaconFarmer
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class BlockBeaconFarmer extends Block(Material.glass) with ITileEntityProvider {
  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileBeaconFarmer()

  override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int,
                                player: EntityPlayer, side: Int,
                                subX: Float, subY: Float, subZ: Float): Boolean = {
    worldIn.getTileEntity(x, y, z).asInstanceOf[TileBeaconFarmer].setInventorySlotContents(0, player.getCurrentEquippedItem)

    true
  }
}
