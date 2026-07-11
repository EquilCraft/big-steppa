package com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis

import com.equilcraft.bigsteppa.Config
import com.equilcraft.bigsteppa.common.alchemy.alchemicalsynthesis.{
  AlchemicalOreDefinition,
  AlchemicalRuneDefinition,
  AlchemicalSynthesisRegistry
}
import com.equilcraft.bigsteppa.common.structure.alchemicalsynthesis.AlchemicalSynthesisStructure
import com.equilcraft.bigsteppa.common.tile.MultiblockController
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.StatCollector
import thaumcraft.api.aspects.AspectList

import java.util

final class TileAlchemicalSynthesisCore
    extends TileEntity
    with MultiblockController[TileAlchemicalSynthesisCore] {

  import TileAlchemicalSynthesisCore._


  override protected def structureDef: IStructureDefinition[TileAlchemicalSynthesisCore] =
    AlchemicalSynthesisStructure.structureDefinition
  override protected def mainPiece: String = "main"
  override protected def horizontalOffset: Int = 5
  override protected def verticalOffset: Int = 1
  override protected def depthOffset: Int = 5


  override protected def onPreStructureCheck(): Unit = runeCounts.clear()
  override protected def onStructureBroken(): Unit = runeCounts.clear()


  private var ticksUntilOreSynthesis = Config.alchemicalSynthesisSpawnInterval
  private val runeCounts = new util.HashMap[String, Integer]()
  private val synthesisCost = new AspectList
  private var calculatedOreWeights = Array.empty[Int]
  private var placementX = Array.empty[Int]
  private var placementY = Array.empty[Int]
  private var placementZ = Array.empty[Int]

  override def updateEntity(): Unit = {
    if (!this.worldObj.isRemote) {
      tickStructureCheck()

      if (Config.alchemicalSynthesisEnabled && this.isStructureFormed) {
        this.ticksUntilOreSynthesis -= 1
        if (this.ticksUntilOreSynthesis <= 0) {
          this.ticksUntilOreSynthesis = this.synthesizeOreVein() match {
            case `synthesisSucceeded` =>
              this.getAcceleratedOperationInterval(Config.alchemicalSynthesisSpawnInterval)
            case `synthesisAreaBlocked` =>
              Config.alchemicalSynthesisSpawnInterval
            case _ =>
              Config.alchemicalSynthesisFailedRetryInterval
          }
        }
      } else {
        this.ticksUntilOreSynthesis = Config.alchemicalSynthesisSpawnInterval
      }
    }
  }

  private def synthesizeOreVein(): Int = {
    val aspectInput = this.getAspectInput
    if (aspectInput == null) return synthesisFailed

    val ores = AlchemicalSynthesisRegistry.getOres
    if (ores.length == 0) return synthesisFailed

    val oreIndex = this.selectOreIndex(ores)
    if (oreIndex < 0) return synthesisFailed

    val ore = ores(oreIndex)
    val naturalVeinSize =
      ore.minimumVeinSize + this.worldObj.rand.nextInt(ore.maximumVeinSize - ore.minimumVeinSize + 1)
    val desiredVeinSize = math.min(
      Config.alchemicalSynthesisMaximumBlocksPerCycle,
      naturalVeinSize + this.getRuneExtraBlocks
    )
    val minimumPlacementCount = math.min(ore.minimumVeinSize, desiredVeinSize)
    this.prepareSynthesisCost(ore, minimumPlacementCount)
    if (!aspectInput.doesContainerContain(this.synthesisCost)) return synthesisFailed

    val placementCount = this.collectOrePlacements(desiredVeinSize)
    if (placementCount < minimumPlacementCount) return synthesisAreaBlocked

    this.prepareSynthesisCost(ore, placementCount)
    if (!aspectInput.doesContainerContain(this.synthesisCost)) return synthesisFailed
    if (!aspectInput.takeFromContainer(this.synthesisCost)) return synthesisFailed

    var placedBlocks = 0
    while (placedBlocks < placementCount) {
      this.worldObj.setBlock(
        this.placementX(placedBlocks),
        this.placementY(placedBlocks),
        this.placementZ(placedBlocks),
        ore.block,
        ore.metadata,
        2
      )
      placedBlocks += 1
    }
    synthesisSucceeded
  }

  private def selectOreIndex(ores: Array[AlchemicalOreDefinition]): Int = {
    if (this.calculatedOreWeights.length != ores.length) {
      this.calculatedOreWeights = new Array[Int](ores.length)
    }

    var oreIndex = 0
    while (oreIndex < ores.length) {
      this.calculatedOreWeights(oreIndex) = ores(oreIndex).baseWeight
      oreIndex += 1
    }

    val runes = AlchemicalSynthesisRegistry.getRunes
    var runeIndex = 0
    while (runeIndex < runes.length) {
      val rune = runes(runeIndex)
      val runeCount = this.getEffectiveRuneCount(rune)
      if (runeCount > 0) {
        var bonusIndex = 0
        while (bonusIndex < rune.oreWeightBonuses.length) {
          val bonus = rune.oreWeightBonuses(bonusIndex)
          val increasedWeight =
            this.calculatedOreWeights(bonus.oreIndex).toLong + bonus.weightPerRune.toLong * runeCount
          this.calculatedOreWeights(bonus.oreIndex) = math.min(increasedWeight, Int.MaxValue).toInt
          bonusIndex += 1
        }
      }
      runeIndex += 1
    }

    var totalWeight = 0L
    oreIndex = 0
    while (oreIndex < this.calculatedOreWeights.length) {
      totalWeight += this.calculatedOreWeights(oreIndex)
      oreIndex += 1
    }
    if (totalWeight <= 0L) return -1

    val selectedWeight = (this.worldObj.rand.nextDouble() * totalWeight).toLong
    var cumulativeWeight = 0L
    oreIndex = 0
    while (oreIndex < this.calculatedOreWeights.length) {
      cumulativeWeight += this.calculatedOreWeights(oreIndex)
      if (selectedWeight < cumulativeWeight) return oreIndex
      oreIndex += 1
    }
    this.calculatedOreWeights.length - 1
  }

  private def getRuneExtraBlocks: Int = {
    val runes = AlchemicalSynthesisRegistry.getRunes
    var extraBlocks = 0L
    var runeIndex = 0
    while (runeIndex < runes.length) {
      val rune = runes(runeIndex)
      extraBlocks += rune.extraBlocksPerRune.toLong * this.getEffectiveRuneCount(rune)
      runeIndex += 1
    }
    math.min(extraBlocks, Config.alchemicalSynthesisMaximumBlocksPerCycle.toLong).toInt
  }

  private def prepareSynthesisCost(ore: AlchemicalOreDefinition, blockCount: Int): Unit = {
    this.synthesisCost.aspects.clear()

    var costIndex = 0
    while (costIndex < ore.aspectCosts.length) {
      val cost = ore.aspectCosts(costIndex)
      this.synthesisCost.add(cost.aspect, cost.amount * blockCount)
      costIndex += 1
    }

    val runes = AlchemicalSynthesisRegistry.getRunes
    var runeIndex = 0
    while (runeIndex < runes.length) {
      val rune = runes(runeIndex)
      val runeCount = this.getEffectiveRuneCount(rune)
      if (runeCount > 0) {
        costIndex = 0
        while (costIndex < rune.aspectCosts.length) {
          val cost = rune.aspectCosts(costIndex)
          this.synthesisCost.add(cost.aspect, cost.amount * runeCount)
          costIndex += 1
        }
      }
      runeIndex += 1
    }
  }

  private def getEffectiveRuneCount(rune: AlchemicalRuneDefinition): Int = {
    if (rune.maximumEffectiveCount <= 0) return 0

    val count = this.runeCounts.get(rune.id)
    if (count == null) 0 else math.min(count.intValue(), rune.maximumEffectiveCount)
  }

  private def collectOrePlacements(desiredCount: Int): Int = {
    this.ensurePlacementCapacity(desiredCount)

    val maximumAttempts = math.max(
      desiredCount,
      desiredCount * Config.alchemicalSynthesisPlacementAttemptsPerBlock
    )
    var attempts = 0
    var placementCount = 0

    while (attempts < maximumAttempts && placementCount == 0) {
      val x = this.xCoord + innerMinimumX + this.worldObj.rand.nextInt(innerSizeX)
      val y = this.yCoord + innerMinimumY + this.worldObj.rand.nextInt(innerSizeY)
      val z = this.zCoord + innerMinimumZ + this.worldObj.rand.nextInt(innerSizeZ)
      if (this.worldObj.isAirBlock(x, y, z)) {
        this.placementX(0) = x
        this.placementY(0) = y
        this.placementZ(0) = z
        placementCount = 1
      }
      attempts += 1
    }

    while (attempts < maximumAttempts && placementCount < desiredCount) {
      val parent = this.worldObj.rand.nextInt(placementCount)
      val direction = this.worldObj.rand.nextInt(veinDirectionX.length)
      val x = this.placementX(parent) + veinDirectionX(direction)
      val y = this.placementY(parent) + veinDirectionY(direction)
      val z = this.placementZ(parent) + veinDirectionZ(direction)

      if (this.isInsideOreSynthesisArea(x, y, z) && this.worldObj.isAirBlock(x, y, z)) {
        var duplicate = false
        var index = 0
        while (index < placementCount && !duplicate) {
          duplicate =
            this.placementX(index) == x &&
              this.placementY(index) == y &&
              this.placementZ(index) == z
          index += 1
        }
        if (!duplicate) {
          this.placementX(placementCount) = x
          this.placementY(placementCount) = y
          this.placementZ(placementCount) = z
          placementCount += 1
        }
      }
      attempts += 1
    }
    placementCount
  }

  private def ensurePlacementCapacity(requiredCapacity: Int): Unit = {
    if (this.placementX.length < requiredCapacity) {
      this.placementX = new Array[Int](requiredCapacity)
      this.placementY = new Array[Int](requiredCapacity)
      this.placementZ = new Array[Int](requiredCapacity)
    }
  }

  private def isInsideOreSynthesisArea(x: Int, y: Int, z: Int): Boolean = {
    val relativeX = x - this.xCoord
    val relativeY = y - this.yCoord
    val relativeZ = z - this.zCoord
    relativeX >= innerMinimumX &&
      relativeX <= innerMaximumX &&
      relativeY >= innerMinimumY &&
      relativeY <= innerMaximumY &&
      relativeZ >= innerMinimumZ &&
      relativeZ <= innerMaximumZ
  }

  def getAcceleratedOperationInterval(baseInterval: Int): Int = {
    val runes = AlchemicalSynthesisRegistry.getRunes
    var speedPercent = 0L
    var runeIndex = 0
    while (runeIndex < runes.length) {
      val rune = runes(runeIndex)
      speedPercent += rune.speedPercentPerRune.toLong * this.getEffectiveRuneCount(rune)
      runeIndex += 1
    }

    val divisor = 100L + speedPercent
    math.max(1L, (baseInterval.toLong * 100L + divisor - 1L) / divisor).toInt
  }

  private def getAspectInput: TileAlchemicalSynthesisAspectInput =
    this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) match {
      case input: TileAlchemicalSynthesisAspectInput => input
      case _                                         => null
    }

  def recordRune(runeId: String): Unit = {
    val currentCount = this.runeCounts.get(runeId)
    this.runeCounts.put(runeId, Int.box(if (currentCount == null) 1 else currentCount.intValue() + 1))
  }

  override def getStructureDescription(trigger: ItemStack): Array[String] =
    Array(
      StatCollector.translateToLocal("structure.bigsteppa.alchemicalSynthesis.core"),
      StatCollector.translateToLocal("structure.bigsteppa.alchemicalSynthesis.rune"),
      StatCollector.translateToLocal("structure.bigsteppa.alchemicalSynthesis.aspectInput"),
      StatCollector.translateToLocal("structure.bigsteppa.alchemicalSynthesis.worldSuction"),
      StatCollector.translateToLocal("structure.bigsteppa.alchemicalSynthesis.oreGeneration")
    )

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    if (compound.hasKey(oreSynthesisTimerTag)) {
      this.ticksUntilOreSynthesis = math.max(1, compound.getInteger(oreSynthesisTimerTag))
    }
  }

  override def writeToNBT(compound: NBTTagCompound): Unit = {
    super.writeToNBT(compound)
    compound.setInteger(oreSynthesisTimerTag, this.ticksUntilOreSynthesis)
  }
}

object TileAlchemicalSynthesisCore {
  private final val synthesisFailed = 0
  private final val synthesisSucceeded = 1
  private final val synthesisAreaBlocked = 2

  private final val oreSynthesisTimerTag = "OreSynthesisTimer"

  private final val innerMinimumX = -4
  private final val innerMaximumX = 4
  private final val innerMinimumY = -6
  private final val innerMaximumY = -1
  private final val innerMinimumZ = -4
  private final val innerMaximumZ = 4
  private final val innerSizeX = innerMaximumX - innerMinimumX + 1
  private final val innerSizeY = innerMaximumY - innerMinimumY + 1
  private final val innerSizeZ = innerMaximumZ - innerMinimumZ + 1

  private val veinDirectionX = Array(1, -1, 0, 0, 0, 0)
  private val veinDirectionY = Array(0, 0, 1, -1, 0, 0)
  private val veinDirectionZ = Array(0, 0, 0, 0, 1, -1)

}
