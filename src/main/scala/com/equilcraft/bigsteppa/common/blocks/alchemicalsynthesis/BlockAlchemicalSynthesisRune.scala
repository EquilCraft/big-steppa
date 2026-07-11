package com.equilcraft.bigsteppa.common.blocks.alchemicalsynthesis

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs

abstract class BlockAlchemicalSynthesisRune(val runeId: String, textureName: String) extends Block(Material.iron) {
  this.setHardness(5.0F)
  this.setResistance(10.0F)
  this.setStepSound(Block.soundTypeMetal)
  this.setCreativeTab(CreativeTabs.tabBlock)
  this.setBlockTextureName(textureName)
}

class BlockBasicAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("basic", "minecraft:iron_block")

class BlockReinforcedAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("reinforced", "minecraft:gold_block")

class BlockResonantAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("resonant", "minecraft:lapis_block")

class BlockStabilizedAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("stabilized", "minecraft:obsidian")

class BlockAbundanceAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("abundance", "minecraft:emerald_block")

class BlockSpeedAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("speed", "minecraft:glowstone")

class BlockAirAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("air", "minecraft:glass")

class BlockFireAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("fire", "minecraft:redstone_block")

class BlockWaterAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("water", "minecraft:ice")

class BlockEarthAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("earth", "minecraft:dirt")

class BlockOrderAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("order", "minecraft:quartz_block_side")

class BlockEntropyAlchemicalSynthesisRune
    extends BlockAlchemicalSynthesisRune("entropy", "minecraft:coal_block")
