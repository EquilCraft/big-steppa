package com.equilcraft.bigsteppa.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.{NetworkManager, Packet}
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import thaumcraft.api.aspects.{Aspect, AspectList, AspectSourceHelper, IAspectContainer, IAspectSource}

final class TileAlchemicalSynthesisAspectInput extends TileEntity with IAspectContainer {
  import TileAlchemicalSynthesisAspectInput._

  private var ticksUntilPull = basePullInterval
  private var nextWorldAspectSource = 0
  private val storedAspects = new AspectList

  override def updateEntity(): Unit = {
    if (!this.worldObj.isRemote) {
      val core = this.getActiveCore
      if (core == null) {
        this.ticksUntilPull = basePullInterval
      } else {
        this.ticksUntilPull -= 1
        if (this.ticksUntilPull <= 0) {
          this.ticksUntilPull =
            if (this.pullOneAspectFromWorld()) core.getAcceleratedOperationInterval(basePullInterval)
            else failedPullRetryInterval
        }
      }
    }
  }

  private def getActiveCore: TileAlchemicalSynthesisCore =
    this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) match {
      case core: TileAlchemicalSynthesisCore if core.isStructureFormed => core
      case _                                                           => null
    }

  private def pullOneAspectFromWorld(): Boolean = {
    val loadedTiles = this.worldObj.loadedTileEntityList
    val tileCount = loadedTiles.size()
    if (tileCount == 0) return false

    if (this.nextWorldAspectSource >= tileCount) {
      this.nextWorldAspectSource = 0
    }

    var checkedTiles = 0
    while (checkedTiles < tileCount) {
      val sourceIndex = (this.nextWorldAspectSource + checkedTiles) % tileCount
      val sourceTile = loadedTiles.get(sourceIndex).asInstanceOf[TileEntity]
      checkedTiles += 1

      sourceTile match {
        case source: IAspectSource if this.isInsideWorldAspectRange(sourceTile) =>
          val sourceAspects = source.getAspects
          if (sourceAspects != null) {
            val aspects = sourceAspects.getAspects
            var aspectIndex = 0
            while (aspectIndex < aspects.length) {
              val aspect = aspects(aspectIndex)
              aspectIndex += 1

              if (
                aspect != null &&
                  this.doesContainerAccept(aspect) &&
                  source.doesContainerContainAmount(aspect, 1)
              ) {
                this.nextWorldAspectSource = (sourceIndex + 1) % tileCount
                if (
                  AspectSourceHelper.drainEssentia(
                    this,
                    aspect,
                    ForgeDirection.UNKNOWN,
                    pullRange
                  )
                ) {
                  this.addToContainer(aspect, 1)
                  return true
                }
                return false
              }
            }
          }
        case _ =>
      }
    }

    this.nextWorldAspectSource = (this.nextWorldAspectSource + 1) % tileCount
    false
  }

  private def isInsideWorldAspectRange(source: TileEntity): Boolean = {
    val offsetX = source.xCoord - this.xCoord
    val offsetY = source.yCoord - this.yCoord
    val offsetZ = source.zCoord - this.zCoord

    math.abs(offsetX) <= pullRange &&
      offsetY >= -pullRange &&
      offsetY < pullRange &&
      math.abs(offsetZ) <= pullRange
  }

  def getRemainingAspectCapacity(aspect: Aspect): Int =
    if (aspect == null) 0
    else math.max(0, maximumAspectAmount - this.storedAspects.getAmount(aspect))

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    this.storedAspects.readFromNBT(compound, aspectStorageTag)
    this.clampStoredAspectAmounts()
  }

  override def writeToNBT(compound: NBTTagCompound): Unit = {
    super.writeToNBT(compound)
    this.storedAspects.writeToNBT(compound, aspectStorageTag)
  }

  override def getDescriptionPacket: Packet = {
    val compound = new NBTTagCompound
    this.storedAspects.writeToNBT(compound, aspectStorageTag)
    new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound)
  }

  override def onDataPacket(network: NetworkManager, packet: S35PacketUpdateTileEntity): Unit = {
    this.storedAspects.readFromNBT(packet.func_148857_g(), aspectStorageTag)
    this.clampStoredAspectAmounts()
  }

  override def getAspects: AspectList =
    this.storedAspects

  override def setAspects(aspects: AspectList): Unit = {
    val incomingAspects = if (aspects == null) null else aspects.copy()
    this.storedAspects.aspects.clear()
    if (incomingAspects != null) {
      incomingAspects.getAspects.filter(_ != null).foreach { aspect =>
        val accepted = math.min(incomingAspects.getAmount(aspect), maximumAspectAmount)
        if (accepted > 0) this.storedAspects.add(aspect, accepted)
      }
    }
    this.markAspectStorageChanged()
  }

  override def doesContainerAccept(aspect: Aspect): Boolean =
    aspect != null && this.getRemainingAspectCapacity(aspect) > 0

  override def addToContainer(aspect: Aspect, amount: Int): Int = {
    if (aspect == null || amount <= 0) return math.max(amount, 0)

    val accepted = math.min(amount, this.getRemainingAspectCapacity(aspect))
    if (accepted > 0) {
      this.storedAspects.add(aspect, accepted)
      this.markAspectStorageChanged()
    }
    amount - accepted
  }

  override def takeFromContainer(aspect: Aspect, amount: Int): Boolean = {
    if (aspect == null || amount <= 0 || this.storedAspects.getAmount(aspect) < amount) return false

    this.storedAspects.remove(aspect, amount)
    this.markAspectStorageChanged()
    true
  }

  override def takeFromContainer(aspects: AspectList): Boolean = {
    if (!this.doesContainerContain(aspects)) return false

    aspects.getAspects.filter(_ != null).foreach { aspect =>
      this.storedAspects.remove(aspect, aspects.getAmount(aspect))
    }
    this.markAspectStorageChanged()
    true
  }

  override def doesContainerContainAmount(aspect: Aspect, amount: Int): Boolean =
    aspect != null && amount >= 0 && this.storedAspects.getAmount(aspect) >= amount

  override def doesContainerContain(aspects: AspectList): Boolean =
    aspects != null &&
      aspects.getAspects.filter(_ != null).forall { aspect =>
        this.storedAspects.getAmount(aspect) >= aspects.getAmount(aspect)
      }

  override def containerContains(aspect: Aspect): Int =
    if (aspect == null) 0 else this.storedAspects.getAmount(aspect)

  private def markAspectStorageChanged(): Unit = {
    this.markDirty()
    if (this.worldObj != null && !this.worldObj.isRemote) {
      this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord)
    }
  }

  private def clampStoredAspectAmounts(): Unit = {
    val aspects = this.storedAspects.getAspects
    var index = 0
    while (index < aspects.length) {
      val aspect = aspects(index)
      if (aspect == null) {
        this.storedAspects.aspects.remove(null)
      } else if (this.storedAspects.getAmount(aspect) > maximumAspectAmount) {
        this.storedAspects.aspects.put(aspect, Int.box(maximumAspectAmount))
      }
      index += 1
    }
  }
}

object TileAlchemicalSynthesisAspectInput {
  private final val basePullInterval = 5
  private final val failedPullRetryInterval = 100
  private final val pullRange = 12
  private final val maximumAspectAmount = 100
  private final val aspectStorageTag = "SynthesisAspects"
}
