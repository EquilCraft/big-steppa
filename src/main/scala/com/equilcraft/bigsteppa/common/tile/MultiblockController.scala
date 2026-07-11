package com.equilcraft.bigsteppa.common.tile

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

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
  protected def structureWorld: World

  /** Interval (in ticks) between automatic structure re-checks. */
  protected def structureCheckInterval: Int = 100

  private var _structureFormed: Boolean = false
  private var _ticksUntilCheck: Int = 0

  def isStructureFormed: Boolean = this._structureFormed

  // === IConstructable implementation ===

  override def construct(trigger: ItemStack, hintsOnly: Boolean): Unit =
    this.structureDef.buildOrHints(
      self, trigger, this.mainPiece, this.structureWorld,
      ExtendedFacing.DEFAULT,
      this.xCoord, this.yCoord, this.zCoord,
      this.horizontalOffset, this.verticalOffset, this.depthOffset,
      hintsOnly
    )

  override def getStructureDefinition: IStructureDefinition[T] = this.structureDef

  // === Periodic structure checking ===

  /** Schedule an immediate structure check on next tick. */
  def scheduleStructureCheck(): Unit = this._ticksUntilCheck = 0

  /** Call from updateEntity() to handle periodic structure checks. */
  protected def tickStructureCheck(): Unit = {
    if (this._ticksUntilCheck > 0) this._ticksUntilCheck -= 1
    if (this._ticksUntilCheck == 0) this.checkStructureNow()
  }

  /** Perform an immediate structure check. Returns true if structure is formed. */
  def checkStructureNow(): Boolean = {
    this.onPreStructureCheck()
    this._structureFormed = this.structureDef.check(
      self, this.mainPiece, this.structureWorld,
      ExtendedFacing.DEFAULT,
      this.xCoord, this.yCoord, this.zCoord,
      this.horizontalOffset, this.verticalOffset, this.depthOffset,
      false
    )
    if (this._structureFormed) this.onStructureFormed()
    else this.onStructureBroken()
    this._ticksUntilCheck = this.structureCheckInterval
    this._structureFormed
  }

  // === Hooks for concrete implementations ===

  /** Called before structure check (e.g. to clear rune counters). */
  protected def onPreStructureCheck(): Unit = ()

  /** Called when structure is successfully formed. */
  protected def onStructureFormed(): Unit = ()

  /** Called when structure validation fails. */
  protected def onStructureBroken(): Unit = ()
}
