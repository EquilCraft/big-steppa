package com.equilcraft.bigsteppa.common.alchemy

import com.equilcraft.bigsteppa.{BigSteppa, Config}
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import thaumcraft.api.aspects.Aspect

import scala.collection.mutable

final case class AlchemicalAspectCost(aspect: Aspect, amount: Int)

final case class AlchemicalOreDefinition(
  id: String,
  block: Block,
  metadata: Int,
  baseWeight: Int,
  minimumVeinSize: Int,
  maximumVeinSize: Int,
  aspectCosts: Array[AlchemicalAspectCost]
)

final case class AlchemicalOreWeightBonus(oreIndex: Int, weightPerRune: Int)

final case class AlchemicalRuneDefinition(
  id: String,
  maximumEffectiveCount: Int,
  extraBlocksPerRune: Int,
  speedPercentPerRune: Int,
  oreWeightBonuses: Array[AlchemicalOreWeightBonus],
  aspectCosts: Array[AlchemicalAspectCost]
)

object AlchemicalSynthesisRegistry {
  @volatile private var ores = Array.empty[AlchemicalOreDefinition]
  @volatile private var runes = Array.empty[AlchemicalRuneDefinition]

  def initialize(): Unit = this.synchronized {
    val parsedOres = mutable.LinkedHashMap.empty[String, AlchemicalOreDefinition]
    Config.alchemicalSynthesisOreDefinitions.foreach { definition =>
      val ore = this.parseOreDefinition(definition)
      if (ore != null) {
        if (parsedOres.contains(ore.id)) {
          BigSteppa.LOG.warn("Duplicate alchemical synthesis ore id '{}'; the last definition wins", ore.id)
        }
        parsedOres.put(ore.id, ore)
      }
    }

    this.ores = parsedOres.values.toArray
    val oreIndices = this.ores.indices.map(index => this.ores(index).id -> index).toMap

    val parsedRunes = mutable.LinkedHashMap.empty[String, AlchemicalRuneDefinition]
    Config.alchemicalSynthesisRuneDefinitions.foreach { definition =>
      val rune = this.parseRuneDefinition(definition, oreIndices)
      if (rune != null) {
        if (parsedRunes.contains(rune.id)) {
          BigSteppa.LOG.warn("Duplicate alchemical synthesis rune id '{}'; the last definition wins", rune.id)
        }
        parsedRunes.put(rune.id, rune)
      }
    }
    this.runes = parsedRunes.values.toArray

    BigSteppa.LOG.info(
      "Loaded {} alchemical synthesis ores and {} rune effects",
      Int.box(this.ores.length),
      Int.box(this.runes.length)
    )
  }

  def getOres: Array[AlchemicalOreDefinition] =
    this.ores

  def getRunes: Array[AlchemicalRuneDefinition] =
    this.runes

  private def parseOreDefinition(definition: String): AlchemicalOreDefinition = {
    try {
      val fields = definition.split(";", -1).map(_.trim)
      if (fields.length != 7) {
        throw new IllegalArgumentException("expected 7 fields")
      }

      val id = fields(0)
      val block = this.findBlock(fields(1))
      val metadata = fields(2).toInt
      val weight = fields(3).toInt
      val minimumVeinSize = fields(4).toInt
      val maximumVeinSize = fields(5).toInt
      val costs = this.parseAspectCosts(fields(6), allowEmpty = false)

      if (id.isEmpty) throw new IllegalArgumentException("empty ore id")
      if (block == null) throw new IllegalArgumentException("unknown block " + fields(1))
      if (metadata < 0 || metadata > 15) throw new IllegalArgumentException("metadata must be in range 0..15")
      if (weight <= 0 || weight > 1000000) {
        throw new IllegalArgumentException("weight must be in range 1..1000000")
      }
      if (minimumVeinSize <= 0) throw new IllegalArgumentException("minimum vein size must be positive")
      if (maximumVeinSize < minimumVeinSize) {
        throw new IllegalArgumentException("maximum vein size is lower than minimum vein size")
      }
      if (maximumVeinSize > 128) throw new IllegalArgumentException("maximum vein size must not exceed 128")
      if (costs == null) throw new IllegalArgumentException("invalid aspect costs")

      AlchemicalOreDefinition(
        id,
        block,
        metadata,
        weight,
        minimumVeinSize,
        maximumVeinSize,
        costs
      )
    } catch {
      case exception: Exception =>
        BigSteppa.LOG.error(
          "Invalid alchemical synthesis ore definition '{}': {}",
          definition,
          exception.getMessage
        )
        null
    }
  }

  private def parseRuneDefinition(
    definition: String,
    oreIndices: Map[String, Int]
  ): AlchemicalRuneDefinition = {
    try {
      val fields = definition.split(";", -1).map(_.trim)
      if (fields.length != 5 && fields.length != 6) {
        throw new IllegalArgumentException("expected 5 legacy fields or 6 current fields")
      }

      val currentFormat = fields.length == 6
      val id = fields(0)
      val maximumEffectiveCount = fields(1).toInt
      val extraBlocksPerRune = fields(2).toInt
      val speedPercentPerRune = if (currentFormat) fields(3).toInt else 0
      val bonuses = this.parseOreBonuses(fields(if (currentFormat) 4 else 3), oreIndices)
      val costs = this.parseAspectCosts(fields(if (currentFormat) 5 else 4), allowEmpty = true)

      if (id.isEmpty) throw new IllegalArgumentException("empty rune id")
      if (maximumEffectiveCount < 0 || maximumEffectiveCount > 80) {
        throw new IllegalArgumentException("maximum effective count must be in range 0..80")
      }
      if (extraBlocksPerRune < 0 || extraBlocksPerRune > 128) {
        throw new IllegalArgumentException("extra blocks per rune must be in range 0..128")
      }
      if (speedPercentPerRune < 0 || speedPercentPerRune > 1000) {
        throw new IllegalArgumentException("speed percent per rune must be in range 0..1000")
      }
      if (bonuses == null) throw new IllegalArgumentException("invalid ore weight bonuses")
      if (costs == null) throw new IllegalArgumentException("invalid aspect costs")

      AlchemicalRuneDefinition(
        id,
        maximumEffectiveCount,
        extraBlocksPerRune,
        speedPercentPerRune,
        bonuses,
        costs
      )
    } catch {
      case exception: Exception =>
        BigSteppa.LOG.error(
          "Invalid alchemical synthesis rune definition '{}': {}",
          definition,
          exception.getMessage
        )
        null
    }
  }

  private def findBlock(registryName: String): Block = {
    val separator = registryName.indexOf(':')
    if (separator <= 0 || separator == registryName.length - 1) return null

    GameRegistry.findBlock(registryName.substring(0, separator), registryName.substring(separator + 1))
  }

  private def parseAspectCosts(value: String, allowEmpty: Boolean): Array[AlchemicalAspectCost] = {
    if (value.isEmpty) {
      return if (allowEmpty) Array.empty[AlchemicalAspectCost] else null
    }

    val costs = mutable.ArrayBuffer.empty[AlchemicalAspectCost]
    value.split(',').foreach { entry =>
      val fields = entry.trim.split("=", -1)
      if (fields.length != 2) return null

      val aspect = Aspect.getAspect(fields(0).trim)
      val amount =
        try fields(1).trim.toInt
        catch {
          case _: NumberFormatException => return null
        }

      if (aspect == null || amount <= 0 || amount > 4096) return null
      costs += AlchemicalAspectCost(aspect, amount)
    }
    costs.toArray
  }

  private def parseOreBonuses(
    value: String,
    oreIndices: Map[String, Int]
  ): Array[AlchemicalOreWeightBonus] = {
    if (value.isEmpty) return Array.empty[AlchemicalOreWeightBonus]

    val bonuses = mutable.ArrayBuffer.empty[AlchemicalOreWeightBonus]
    value.split(',').foreach { entry =>
      val fields = entry.trim.split("=", -1)
      if (fields.length != 2) return null

      val oreIndex = oreIndices.getOrElse(fields(0).trim, -1)
      val weight =
        try fields(1).trim.toInt
        catch {
          case _: NumberFormatException => return null
        }

      if (oreIndex < 0 || weight < 0 || weight > 1000000) return null
      bonuses += AlchemicalOreWeightBonus(oreIndex, weight)
    }
    bonuses.toArray
  }
}
