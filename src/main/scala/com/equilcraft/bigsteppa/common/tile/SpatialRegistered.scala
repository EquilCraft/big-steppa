package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry
import net.minecraft.tileentity.TileEntity

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

  protected def spatialValidate(): Unit = {
    if (!this.worldObj.isRemote) {
      spatialRegistry.add(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
    }
  }

  protected def spatialInvalidate(): Unit = {
    if (!this.worldObj.isRemote) {
      spatialRegistry.remove(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
    }
  }
}
