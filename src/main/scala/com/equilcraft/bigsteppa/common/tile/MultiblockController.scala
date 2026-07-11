package com.equilcraft.bigsteppa.common.tile

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity

/**
 * Base trait for multiblock structure controllers.
 * Encapsulates all structureLib boilerplate: construct, check, periodic validation, and lifecycle hooks.
 *
 * @tparam T the concrete TileEntity type (self-type bound)
 */
trait MultiblockController[T <: TileEntity] extends IConstructable { self: T =>

  /** IStructureDefinition for this structure (typically a lazy val in the companion object). */
  protected def structureDef: IStructureDefinition[T]

  /** Name of the main structure piece. */
  protected def mainPiece: String

  /** Controller offsets within the structure. */
  protected def horizontalOffset: Int
  protected def verticalOffset: Int
  protected def depthOffset: Int

  /** Interval (in ticks) between automatic structure re-checks. */
  protected def structureCheckInterval: Int = 100

  private var _structureFormed: Boolean = false
  private var _ticksUntilCheck: Int = 0

  def isStructureFormed: Boolean = _structureFormed

  // === IConstructable implementation ===

  override def construct(trigger: ItemStack, hintsOnly: Boolean): Unit =
    structureDef.buildOrHints(
      self, trigger, mainPiece, worldObj,
      ExtendedFacing.DEFAULT,
      xCoord, yCoord, zCoord,
      horizontalOffset, verticalOffset, depthOffset,
      hintsOnly
    )

  override def getStructureDefinition: IStructureDefinition[T] = structureDef

  // === Periodic structure checking ===

  /** Schedule an immediate structure check on next tick. */
  def scheduleStructureCheck(): Unit = _ticksUntilCheck = 0

  /** Call from updateEntity() to handle periodic structure checks. */
  protected def tickStructureCheck(): Unit = {
    if (_ticksUntilCheck > 0) _ticksUntilCheck -= 1
    if (_ticksUntilCheck == 0) checkStructureNow()
  }

  /** Perform an immediate structure check. Returns true if structure is formed. */
  def checkStructureNow(): Boolean = {
    onPreStructureCheck()
    _structureFormed = structureDef.check(
      self, mainPiece, worldObj,
      ExtendedFacing.DEFAULT,
      xCoord, yCoord, zCoord,
      horizontalOffset, verticalOffset, depthOffset,
      false
    )
    if (_structureFormed) onStructureFormed()
    else onStructureBroken()
    _ticksUntilCheck = structureCheckInterval
    _structureFormed
  }

  // === Hooks for concrete implementations ===

  /** Called before structure check (e.g. to clear rune counters). */
  protected def onPreStructureCheck(): Unit = ()

  /** Called when structure is successfully formed. */
  protected def onStructureFormed(): Unit = ()

  /** Called when structure validation fails. */
  protected def onStructureBroken(): Unit = ()
}
