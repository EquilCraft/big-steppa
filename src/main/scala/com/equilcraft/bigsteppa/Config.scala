package com.equilcraft.bigsteppa

import net.minecraftforge.common.config.Configuration

import java.io.File

object Config {
  private final val alchemicalSynthesisGeneralCategory = "alchemical_synthesis.general"
  private final val alchemicalSynthesisOresCategory = "alchemical_synthesis.ores"
  private final val alchemicalSynthesisRunesCategory = "alchemical_synthesis.runes"
  private final val defaultSpeedRuneDefinition = "speed;8;0;15;;motus=1,potentia=1"

  private val defaultAlchemicalSynthesisOres = Array(
    "minecraft.coal;minecraft:coal_ore;0;320;3;8;terra=1,ignis=1",
    "minecraft.iron;minecraft:iron_ore;0;160;2;6;metallum=2",
    "minecraft.gold;minecraft:gold_ore;0;16;2;5;metallum=2,lucrum=1",
    "minecraft.redstone;minecraft:redstone_ore;0;56;3;7;potentia=2",
    "minecraft.diamond;minecraft:diamond_ore;0;7;2;4;vitreus=3,lucrum=2",
    "minecraft.lapis;minecraft:lapis_ore;0;6;3;6;sensus=2,aqua=1",
    "minecraft.emerald;minecraft:emerald_ore;0;5;2;3;vitreus=2,lucrum=3",
    "minecraft.quartz;minecraft:quartz_ore;0;208;3;8;vitreus=2,ignis=1",
    "thaumcraft.cinnabar;Thaumcraft:blockCustomOre;0;18;2;4;terra=1,permutatio=1",
    "thaumcraft.infused_air;Thaumcraft:blockCustomOre;1;8;2;5;aer=2,praecantatio=1",
    "thaumcraft.infused_fire;Thaumcraft:blockCustomOre;2;8;2;5;ignis=2,praecantatio=1",
    "thaumcraft.infused_water;Thaumcraft:blockCustomOre;3;8;2;5;aqua=2,praecantatio=1",
    "thaumcraft.infused_earth;Thaumcraft:blockCustomOre;4;8;2;5;terra=2,praecantatio=1",
    "thaumcraft.infused_order;Thaumcraft:blockCustomOre;5;8;2;5;ordo=2,praecantatio=1",
    "thaumcraft.infused_entropy;Thaumcraft:blockCustomOre;6;8;2;5;perditio=2,praecantatio=1",
    "thaumcraft.amber;Thaumcraft:blockCustomOre;7;20;2;4;vitreus=2,arbor=1"
  )

  private val defaultAlchemicalSynthesisRunes = Array(
    "basic;0;0;0;;",
    "reinforced;8;0;0;minecraft.iron=24,minecraft.gold=4,thaumcraft.cinnabar=4;metallum=1",
    "resonant;8;0;0;minecraft.redstone=10,minecraft.lapis=4,minecraft.diamond=2,minecraft.quartz=12,thaumcraft.amber=4;potentia=1",
    "stabilized;8;0;0;thaumcraft.infused_order=10;ordo=1",
    "abundance;8;1;0;;permutatio=1,terra=1",
    defaultSpeedRuneDefinition,
    "air;8;0;0;thaumcraft.infused_air=20;aer=1",
    "fire;8;0;0;thaumcraft.infused_fire=20;ignis=1",
    "water;8;0;0;thaumcraft.infused_water=20;aqua=1",
    "earth;8;0;0;thaumcraft.infused_earth=20;terra=1",
    "order;8;0;0;thaumcraft.infused_order=20;ordo=1",
    "entropy;8;0;0;thaumcraft.infused_entropy=20;perditio=1"
  )

  var greeting = "Hello World"
  var alchemicalSynthesisEnabled = true
  var alchemicalSynthesisSpawnInterval = 200
  var alchemicalSynthesisFailedRetryInterval = 20
  var alchemicalSynthesisMaximumBlocksPerCycle = 24
  var alchemicalSynthesisPlacementAttemptsPerBlock = 20
  var alchemicalSynthesisOreDefinitions: Array[String] = this.defaultAlchemicalSynthesisOres
  var alchemicalSynthesisRuneDefinitions: Array[String] = this.defaultAlchemicalSynthesisRunes

  def synchronizeConfiguration(configFile: File): Unit = {
    val configuration = new Configuration(configFile)
    this.greeting =
      configuration.getString("greeting", Configuration.CATEGORY_GENERAL, this.greeting, "How shall I greet?")
    this.alchemicalSynthesisEnabled = configuration.getBoolean(
      "enabled",
      alchemicalSynthesisGeneralCategory,
      this.alchemicalSynthesisEnabled,
      "Enables ore synthesis inside a completed structure."
    )
    this.alchemicalSynthesisSpawnInterval = configuration.getInt(
      "spawnIntervalTicks",
      alchemicalSynthesisGeneralCategory,
      this.alchemicalSynthesisSpawnInterval,
      1,
      72000,
      "Ticks between successful ore synthesis attempts."
    )
    this.alchemicalSynthesisFailedRetryInterval = configuration.getInt(
      "failedRetryTicks",
      alchemicalSynthesisGeneralCategory,
      this.alchemicalSynthesisFailedRetryInterval,
      1,
      72000,
      "Ticks before retrying when there is not enough essentia or free space."
    )
    this.alchemicalSynthesisMaximumBlocksPerCycle = configuration.getInt(
      "maximumBlocksPerCycle",
      alchemicalSynthesisGeneralCategory,
      this.alchemicalSynthesisMaximumBlocksPerCycle,
      1,
      128,
      "Hard limit for one generated ore vein after rune bonuses."
    )
    this.alchemicalSynthesisPlacementAttemptsPerBlock = configuration.getInt(
      "placementAttemptsPerBlock",
      alchemicalSynthesisGeneralCategory,
      this.alchemicalSynthesisPlacementAttemptsPerBlock,
      1,
      100,
      "Random-walk attempts used to find a free position for each ore block."
    )
    this.alchemicalSynthesisOreDefinitions = configuration.getStringList(
      "definitions",
      alchemicalSynthesisOresCategory,
      this.defaultAlchemicalSynthesisOres,
      "id;modid:block;meta;baseWeight;minVein;maxVein;aspect=amount,aspect=amount"
    )
    this.alchemicalSynthesisRuneDefinitions = configuration.getStringList(
      "definitions",
      alchemicalSynthesisRunesCategory,
      this.defaultAlchemicalSynthesisRunes,
      "runeId;maxEffectiveCount;extraBlocksPerRune;speedPercentPerRune;oreId=weightBonus,...;aspect=amount,..."
    )
    if (!this.alchemicalSynthesisRuneDefinitions.exists(_.trim.startsWith("speed;"))) {
      this.alchemicalSynthesisRuneDefinitions =
        this.alchemicalSynthesisRuneDefinitions :+ defaultSpeedRuneDefinition
      configuration
        .get(alchemicalSynthesisRunesCategory, "definitions", this.defaultAlchemicalSynthesisRunes)
        .set(this.alchemicalSynthesisRuneDefinitions)
    }
    if (configuration.hasChanged) configuration.save()
  }
}
