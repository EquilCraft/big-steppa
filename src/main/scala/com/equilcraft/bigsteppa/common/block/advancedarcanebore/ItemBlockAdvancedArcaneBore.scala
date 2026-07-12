package com.equilcraft.bigsteppa.common.block.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import com.equilcraft.bigsteppa.common.init.SteppaBlocks
import net.minecraft.block.{Block, BlockPistonBase}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

final class ItemBlockAdvancedArcaneBore(block: Block) extends ItemBlock(block) {
  this.setMaxDamage(0)
  this.setHasSubtypes(true)

  override def getMetadata(damage: Int): Int = 5

  override def placeBlockAt(
    stack: ItemStack,
    player: EntityPlayer,
    world: World,
    x: Int,
    y: Int,
    z: Int,
    side: Int,
    hitX: Float,
    hitY: Float,
    hitZ: Float,
    metadata: Int
  ): Boolean = {
    if (side != 1 || world.getBlock(x, y - 1, z) != SteppaBlocks.advancedArcaneBoreBase) return false
    val placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, 5)
    if (placed) {
      world.getTileEntity(x, y, z) match {
        case bore: TileAdvancedArcaneBore =>
          bore.baseOrientation = ForgeDirection.getOrientation(side)
          bore.setOrientation(
            ForgeDirection.getOrientation(BlockPistonBase.determineOrientation(world, x, y, z, player)),
            true
          )
          bore.markDirty()
          world.markBlockForUpdate(x, y, z)
        case _ =>
      }
    }
    placed
  }

  override def func_150936_a(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    side: Int,
    player: EntityPlayer,
    stack: ItemStack
  ): Boolean = side == 1 && world.getBlock(x, y, z) == SteppaBlocks.advancedArcaneBoreBase
}
