package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Optional trait for TileEntities that need spatial proximity search registration.
 * Automatically registers/unregisters the tile in a BlocksChaosStructureRegistry
 * on validate/invalidate (server-side only).
 *
 * Can be mixed into any TileEntity — multiblock controllers or standalone tiles alike.
 *
 * @tparam T the concrete TileEntity type (self-type bound)
 */
trait SpatialRegistered[T <: TileEntity] { self: T =>

  /** The registry instance this tile registers with (typically defined in the companion object). */
  protected def spatialRegistry: BlocksChaosStructureRegistry[T]

  protected def spatialWorld: World

  protected def spatialValidate(): Unit = {
    val world = this.spatialWorld
    if (!world.isRemote) {
      this.spatialRegistry.add(world, this.xCoord, this.yCoord, this.zCoord)
    }
  }

  protected def spatialInvalidate(): Unit = {
    val world = this.spatialWorld
    if (!world.isRemote) {
      this.spatialRegistry.remove(world, this.xCoord, this.yCoord, this.zCoord)
    }
  }
}
