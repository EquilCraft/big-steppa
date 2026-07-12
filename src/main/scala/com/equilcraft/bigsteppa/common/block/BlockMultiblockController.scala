package com.equilcraft.bigsteppa.common.block

import com.equilcraft.bigsteppa.common.tile.MultiblockController
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World

/**
 * Base block for multiblock structure controllers.
 * Handles the common onBlockActivated (check structure + send chat message)
 * and onNeighborBlockChange (schedule structure recheck) patterns.
 *
 * @tparam T the concrete TileEntity type that implements MultiblockController
 */
abstract class BlockMultiblockController[T <: TileEntity with MultiblockController[T]](
  material: Material
) extends Block(material) with ITileEntityProvider {

  /** Localization key for "structure formed" chat message. */
  protected def formedTranslationKey: String

  /** Localization key for "structure incomplete" chat message. */
  protected def incompleteTranslationKey: String

  override def onBlockActivated(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    player: EntityPlayer,
    side: Int,
    hitX: Float,
    hitY: Float,
    hitZ: Float
  ): Boolean = {
    if (!world.isRemote) {
      world.getTileEntity(x, y, z) match {
        case controller: MultiblockController[_] =>
          val key =
            if (controller.checkStructureNow()) this.formedTranslationKey
            else this.incompleteTranslationKey
          player.addChatMessage(new ChatComponentTranslation(key))
        case _ =>
      }
    }
    true
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, neighbor: Block): Unit = {
    world.getTileEntity(x, y, z) match {
      case controller: MultiblockController[_] => controller.scheduleStructureCheck()
      case _ =>
    }
  }
}
