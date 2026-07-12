package com.equilcraft.bigsteppa.common.block.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBoreBase
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

final class ItemBlockAdvancedArcaneBoreBase(block: Block) extends ItemBlock(block) {
  this.setMaxDamage(0)
  this.setHasSubtypes(false)

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
    val placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)
    if (placed) {
      world.getTileEntity(x, y, z) match {
        case base: TileAdvancedArcaneBoreBase =>
          val rotation = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3
          base.orientation = rotation match {
            case 0 => ForgeDirection.SOUTH
            case 1 => ForgeDirection.WEST
            case 2 => ForgeDirection.NORTH
            case _ => ForgeDirection.EAST
          }
          base.markDirty()
          world.markBlockForUpdate(x, y, z)
        case _ =>
      }
    }
    placed
  }
}
