package com.equilcraft.bigsteppa.common.tile.advancedarcanebore

import net.minecraft.block.Block
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.{ItemPickaxe, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.{FakePlayer, FakePlayerFactory, ForgeDirection}
import net.minecraftforge.event.ForgeEventFactory
import com.mojang.authlib.GameProfile
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import thaumcraft.api.wands.{FocusUpgradeType, ItemFocusBasic}
import thaumcraft.common.items.equipment.ItemElementalPickaxe
import thaumcraft.common.items.wands.foci.ItemFocusExcavation
import thaumcraft.common.lib.utils.{BlockUtils, InventoryUtils, Utils}
import thaumcraft.common.tiles.TileArcaneBore

import java.util.{ArrayList, UUID}
import java.util.ArrayList

final class TileAdvancedArcaneBore extends TileArcaneBore {
  private var excavationWidthValue = TileAdvancedArcaneBore.minWidth
  private var excavationDepthValue = TileAdvancedArcaneBore.defaultDepth
  private var perditio = 0
  private var spiralValue = 0
  private var currentRadiusValue = 0.0F
  private var radialIncrement = 0.0F
  private var lastSpiralX = 0
  private var lastSpiralY = 0
  private var lastSpiralZ = 0
  private var searchMisses = 0
  private var targetX = 0
  private var targetY = 0
  private var targetZ = 0
  private var miningTicks = 0
  private var hasTarget = false
  private var areaComplete = false
  private var clientActive = false
  private var completionProbeTicks = 0
  private var suppressVanillaDig = false
  private var boreBase: TileAdvancedArcaneBoreBase = null
  private var ownerId: UUID = null
  private var ownerName = ""
  private var ownerFakePlayer: FakePlayer = null

  def excavationWidth: Int = this.excavationWidthValue

  def storedPerditio: Int = this.perditio

  def excavationDepth: Int = this.excavationDepthValue

  def setOwner(player: EntityPlayer): Unit = {
    this.ownerId = player.getUniqueID
    this.ownerName = player.getCommandSenderName
    this.ownerFakePlayer = null
    this.markDirty()
  }

  def setExcavationDepth(requested: Int): Unit = {
    val next = math.max(1, math.min(TileAdvancedArcaneBore.maxDepth, requested))
    if (this.excavationDepthValue != next) {
      this.excavationDepthValue = next
      this.resetMining()
      this.markDirty()
      if (this.worldObj != null) this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord)
    }
  }

  def setStoredPerditio(amount: Int): Unit = {
    this.perditio = math.max(0, math.min(TileAdvancedArcaneBore.capacity, amount))
  }

  def setExcavationWidth(requested: Int): Unit = {
    val bounded = math.max(TileAdvancedArcaneBore.minWidth, math.min(TileAdvancedArcaneBore.maxWidth, requested))
    val next = if ((bounded & 1) == 0) bounded - 1 else bounded
    if (this.excavationWidthValue != next) {
      this.excavationWidthValue = next
      this.area = (next - 5) / 2
      this.resetMining()
      this.markDirty()
      if (this.worldObj != null) this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord)
    }
  }

  override def updateEntity(): Unit = {
    if (this.worldObj.isRemote) {
      if (this.clientActive && !this.areaComplete) super.updateEntity()
    }
    else {
      this.suppressVanillaDig = true
      try super.updateEntity()
      finally this.suppressVanillaDig = false
      if (!this.gettingPower) {
        this.resetMining()
      }
      if (this.areaComplete && this.pickaxe != null && this.focus != null) this.probeCompletedArea()
      if (!this.areaComplete) this.pullPerditio()
      val ready = this.isReady
      if (ready) this.mineTick() else this.hasTarget = false
      this.setClientActive(ready && !this.areaComplete && this.hasTarget)
    }
  }

  override def gettingPower: Boolean =
    if (this.suppressVanillaDig) false else super.gettingPower

  override def markDirty(): Unit = {
    super.markDirty()
    this.area = (this.excavationWidthValue - 5) / 2
  }

  override def setOrientation(direction: ForgeDirection, initial: Boolean): Unit = {
    super.setOrientation(direction, initial)
    this.resetMining()
  }

  override def getInventoryName: String = "container.bigsteppa.advancedArcaneBore"

  override def hasCustomInventoryName: Boolean = true

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean =
    (slot == 0 && stack != null && stack.getItem.isInstanceOf[ItemFocusExcavation]) ||
      (slot == 1 && stack != null && stack.getItem.isInstanceOf[ItemPickaxe])

  override def readCustomNBT(tag: NBTTagCompound): Unit = {
    super.readCustomNBT(tag)
    val savedAreaComplete = tag.hasKey("AreaComplete") && tag.getBoolean("AreaComplete")
    val requestedWidth = if (tag.hasKey("ExcavationWidth")) tag.getInteger("ExcavationWidth") else TileAdvancedArcaneBore.minWidth
    this.excavationWidthValue = TileAdvancedArcaneBore.normalizeWidth(requestedWidth)
    this.excavationDepthValue = math.max(1, math.min(TileAdvancedArcaneBore.maxDepth, if (tag.hasKey("ExcavationDepth")) tag.getInteger("ExcavationDepth") else TileAdvancedArcaneBore.defaultDepth))
    this.perditio = math.max(0, math.min(TileAdvancedArcaneBore.capacity, tag.getInteger("Perditio")))
    this.ownerName = if (tag.hasKey("OwnerName")) tag.getString("OwnerName") else ""
    this.ownerId = if (tag.hasKey("OwnerMost") && tag.hasKey("OwnerLeast")) {
      new UUID(tag.getLong("OwnerMost"), tag.getLong("OwnerLeast"))
    } else null
    this.area = (this.excavationWidthValue - 5) / 2
    this.resetMining()
    this.areaComplete = savedAreaComplete
    this.clientActive = tag.hasKey("ClientActive") && tag.getBoolean("ClientActive")
    this.completionProbeTicks = if (savedAreaComplete) TileAdvancedArcaneBore.completionProbeInterval else 0
  }

  override def writeCustomNBT(tag: NBTTagCompound): Unit = {
    super.writeCustomNBT(tag)
    tag.setInteger("ExcavationWidth", this.excavationWidthValue)
    tag.setInteger("ExcavationDepth", this.excavationDepthValue)
    tag.setInteger("Perditio", this.perditio)
    tag.setBoolean("AreaComplete", this.areaComplete)
    tag.setBoolean("ClientActive", this.clientActive)
    tag.setString("OwnerName", this.ownerName)
    if (this.ownerId != null) {
      tag.setLong("OwnerMost", this.ownerId.getMostSignificantBits)
      tag.setLong("OwnerLeast", this.ownerId.getLeastSignificantBits)
    }
  }

  private def isReady: Boolean = {
    val pick = this.pickaxe
    this.gettingPower && this.boreBase != null && this.ownerId != null && this.hasFocus && this.hasPickaxe && pick != null && pick.isItemStackDamageable && this.perditio > 0
  }

  private def pickaxe: ItemStack = {
    val stack = this.getStackInSlot(1)
    if (stack != null && stack.getItem.isInstanceOf[ItemPickaxe]) stack else null
  }

  private def focus: ItemStack = {
    val stack = this.getStackInSlot(0)
    if (stack != null && stack.getItem.isInstanceOf[ItemFocusExcavation]) stack else null
  }

  private def resetMining(): Unit = {
    this.spiralValue = 0
    this.currentRadiusValue = 0.0F
    this.radialIncrement = 0.0F
    this.lastSpiralX = 0
    this.lastSpiralY = 0
    this.lastSpiralZ = 0
    this.searchMisses = 0
    this.miningTicks = 0
    this.hasTarget = false
    this.areaComplete = false
    this.completionProbeTicks = 0
  }

  private def pullPerditio(): Unit = {
    this.boreBase = this.findBase()
    if (this.boreBase != null && this.perditio < TileAdvancedArcaneBore.capacity && this.boreBase.drawPerditio()) {
      this.perditio += 1
    }
  }

  private def findBase(): TileAdvancedArcaneBoreBase = {
    val direction = this.baseOrientation.getOpposite
    this.worldObj.getTileEntity(
      this.xCoord + direction.offsetX,
      this.yCoord + direction.offsetY,
      this.zCoord + direction.offsetZ
    ) match {
      case base: TileAdvancedArcaneBoreBase => base
      case _ => null
    }
  }

  private def mineTick(): Unit = {
    if (this.areaComplete) return
    val currentPick = this.pickaxe
    val currentMiner = this.getFakePlayer
    if (currentPick == null || currentMiner == null) return
    this.equipFakePlayer(currentMiner, currentPick)
    if (!this.hasTarget) this.findTarget()
    if (!this.hasTarget) return
    if (this.miningTicks > 0) {
      this.miningTicks -= 1
      return
    }
    this.mineTarget()
    this.hasTarget = false
    if (!this.areaComplete && this.isReady) this.findTarget()
  }

  private def probeCompletedArea(): Unit = {
    this.completionProbeTicks -= 1
    if (this.completionProbeTicks > 0) return
    this.completionProbeTicks = TileAdvancedArcaneBore.completionProbeInterval
    this.setAreaComplete(false)
    this.hasTarget = false
    this.findTarget()
  }

  private def findTarget(): Unit = {
    var attempts = 0
    while (attempts < TileAdvancedArcaneBore.searchStepsPerTick && !this.hasTarget && !this.areaComplete) {
      if (this.findNextOriginalTarget()) {
        this.searchMisses = 0
      } else {
        this.searchMisses += 1
        if (this.searchMisses >= math.max(TileAdvancedArcaneBore.completionSearchLimit, this.excavationWidthValue * this.excavationWidthValue * 4)) {
          this.setAreaComplete(true)
        }
      }
      attempts += 1
    }
    if (this.areaComplete) {
      this.completionProbeTicks = TileAdvancedArcaneBore.completionProbeInterval
    }
  }

  private def setAreaComplete(value: Boolean): Unit = {
    if (this.areaComplete == value) return
    this.areaComplete = value
    if (value) this.setClientActive(false)
    this.markDirty()
    if (this.worldObj != null && !this.worldObj.isRemote) {
      this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord)
    }
  }

  private def setClientActive(value: Boolean): Unit = {
    if (this.clientActive == value) return
    this.clientActive = value
    this.markDirty()
    if (this.worldObj != null && !this.worldObj.isRemote) {
      this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord)
    }
  }

  private def findNextOriginalTarget(): Boolean = {
    if (this.radialIncrement == 0.0F) {
      this.radialIncrement = this.excavationWidthValue / 2.0F / 360.0F
    }
    var x = this.lastSpiralX
    var y = this.lastSpiralY
    var z = this.lastSpiralZ
    while (x == this.lastSpiralX && y == this.lastSpiralY && z == this.lastSpiralZ) {
      this.spiralValue += 2
      if (this.spiralValue >= 360) this.spiralValue -= 360
      this.currentRadiusValue += this.radialIncrement
      if (this.currentRadiusValue > this.excavationWidthValue / 2.0F || this.currentRadiusValue < -(this.excavationWidthValue / 2.0F)) {
        this.radialIncrement *= -1.0F
      }
      val source = Vec3.createVectorHelper(
        this.xCoord + this.orientation.offsetX + 0.5D,
        this.yCoord + this.orientation.offsetY + 0.5D,
        this.zCoord + this.orientation.offsetZ + 0.5D
      )
      val target = Vec3.createVectorHelper(0.0D, this.currentRadiusValue, 0.0D)
      target.rotateAroundZ(this.spiralValue.toFloat / 180.0F * math.Pi.toFloat)
      target.rotateAroundY((math.Pi / 2.0D * this.orientation.offsetX).toFloat)
      target.rotateAroundX((math.Pi / 2.0D * this.orientation.offsetY).toFloat)
      val result = source.addVector(target.xCoord, target.yCoord, target.zCoord)
      x = math.floor(result.xCoord).toInt
      y = math.floor(result.yCoord).toInt
      z = math.floor(result.zCoord).toInt
    }
    this.lastSpiralX = x
    this.lastSpiralY = y
    this.lastSpiralZ = z
    x += this.orientation.offsetX
    y += this.orientation.offsetY
    z += this.orientation.offsetZ
    var depth = 0
    while (depth < this.excavationDepthValue) {
      x += this.orientation.offsetX
      y += this.orientation.offsetY
      z += this.orientation.offsetZ
      val block = this.worldObj.getBlock(x, y, z)
      val metadata = this.worldObj.getBlockMetadata(x, y, z)
      if (block.getBlockHardness(this.worldObj, x, y, z) < 0.0F) return false
      if (!block.isAir(this.worldObj, x, y, z) && block.canCollideCheck(metadata, false) && block.getCollisionBoundingBoxFromPool(this.worldObj, x, y, z) != null) {
        this.targetX = x
        this.targetY = y
        this.targetZ = z
        val hardness = block.getBlockHardness(this.worldObj, x, y, z)
        this.miningTicks = math.max(1, math.max(10 - this.speed, (hardness * 2.0F).toInt - this.speed * 2))
        val start = Vec3.createVectorHelper(
          this.xCoord + this.orientation.offsetX + 0.5D,
          this.yCoord + this.orientation.offsetY + 0.5D,
          this.zCoord + this.orientation.offsetZ + 0.5D
        )
        val end = Vec3.createVectorHelper(x + 0.5D, y + 0.5D, z + 0.5D)
        val hit = this.worldObj.func_147447_a(start, end, false, true, false)
        if (hit != null) {
          val hitBlock = this.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ)
          if (hitBlock.getBlockHardness(this.worldObj, hit.blockX, hit.blockY, hit.blockZ) > -1.0F && hitBlock.getCollisionBoundingBoxFromPool(this.worldObj, hit.blockX, hit.blockY, hit.blockZ) != null) {
            this.targetX = hit.blockX
            this.targetY = hit.blockY
            this.targetZ = hit.blockZ
            this.miningTicks = math.max(1, math.max(10 - this.speed, (hitBlock.getBlockHardness(this.worldObj, hit.blockX, hit.blockY, hit.blockZ) * 2.0F).toInt - this.speed * 2))
          }
        }
        this.hasTarget = true
        this.notifyDigTarget()
        return true
      }
      depth += 1
    }
    false
  }

  private def miningDuration(block: Block, metadata: Int, hardness: Float): Int = {
    val pick = this.pickaxe
    val baseSpeed = math.max(1.0F, pick.getItem.getDigSpeed(pick, block, metadata))
    val efficiency = EnchantmentHelper.getEfficiencyModifier(this.getFakePlayer)
    val efficiencySpeed = if (baseSpeed > 1.0F && efficiency > 0) efficiency * efficiency + 1 else 0
    val potencySpeed = this.focusUpgrade(FocusUpgradeType.potency) * 2
    math.max(1, math.ceil(hardness * 30.0D / (baseSpeed + efficiencySpeed + potencySpeed)).toInt)
  }

  private def notifyDigTarget(): Unit = {
    val x = this.targetX - this.xCoord + 64
    val y = this.targetY - this.yCoord + 64
    val z = this.targetZ - this.zCoord + 64
    this.getDigEvent((x & 255) << 16 | (y & 255) << 8 | z & 255)
    this.sendDigEvent()
  }

  private def mineTarget(): Unit = {
    val pick = this.pickaxe
    if (pick == null || this.perditio <= 0) return
    val block = this.worldObj.getBlock(this.targetX, this.targetY, this.targetZ)
    if (block.isAir(this.worldObj, this.targetX, this.targetY, this.targetZ)) return
    val metadata = this.worldObj.getBlockMetadata(this.targetX, this.targetY, this.targetZ)
    if (block.getBlockHardness(this.worldObj, this.targetX, this.targetY, this.targetZ) < 0.0F) return
    val miner = this.getFakePlayer
    this.equipFakePlayer(miner, pick)
    miner.setPosition(this.targetX + 0.5D, this.targetY + 0.5D, this.targetZ + 0.5D)
    val breakEvent = new BlockEvent.BreakEvent(
      this.targetX,
      this.targetY,
      this.targetZ,
      this.worldObj,
      block,
      metadata,
      miner
    )
    MinecraftForge.EVENT_BUS.post(breakEvent)
    if (breakEvent.isCanceled) return
    val silk = block.canSilkHarvest(this.worldObj, miner, this.targetX, this.targetY, this.targetZ, metadata) &&
      (EnchantmentHelper.getSilkTouchModifier(miner) || this.focusUpgrade(FocusUpgradeType.silktouch) > 0)
    val fortune = math.max(EnchantmentHelper.getFortuneModifier(miner), this.focusUpgrade(FocusUpgradeType.treasure))
    val doTileDrops = this.worldObj.getGameRules.getGameRuleBooleanValue("doTileDrops")
    val drops = if (doTileDrops) {
      if (silk) {
        val result = new ArrayList[ItemStack]()
        val stacked = BlockUtils.createStackedBlock(block, metadata)
        if (stacked != null) result.add(stacked)
        result
      } else block.getDrops(this.worldObj, this.targetX, this.targetY, this.targetZ, metadata, fortune)
    } else new ArrayList[ItemStack]()
    if (doTileDrops) {
      ForgeEventFactory.fireBlockHarvesting(
        drops,
        this.worldObj,
        block,
        this.targetX,
        this.targetY,
        this.targetZ,
        metadata,
        fortune,
        1.0F,
        silk,
        miner
      )
    }
    this.collectNearbyDrops(drops)
    this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType, 99, Block.getIdFromBlock(block) + (metadata << 12))
    pick.func_150999_a(this.worldObj, block, this.targetX, this.targetY, this.targetZ, miner)
    this.worldObj.setBlockToAir(this.targetX, this.targetY, this.targetZ)
    var index = 0
    while (index < drops.size()) {
      this.outputDrop(this.specialMiningResult(drops.get(index), silk))
      index += 1
    }
    this.perditio -= 1
    if (pick.stackSize <= 0) this.setInventorySlotContents(1, null)
    else this.setInventorySlotContents(1, pick)
  }

  private def equipFakePlayer(miner: FakePlayer, pick: ItemStack): Unit = {
    miner.inventory.currentItem = 0
    miner.inventory.mainInventory(0) = pick
  }

  private def focusUpgrade(upgrade: FocusUpgradeType): Int = this.focus match {
    case null => 0
    case stack => stack.getItem.asInstanceOf[ItemFocusBasic].getUpgradeLevel(stack, upgrade)
  }

  private def effectiveFortune: Int =
    math.max(EnchantmentHelper.getFortuneModifier(this.getFakePlayer), this.focusUpgrade(FocusUpgradeType.treasure))

  private def specialMiningResult(stack: ItemStack, silk: Boolean): ItemStack =
    if (!silk && this.hasDowsing) Utils.findSpecialMiningResult(stack, 0.2F + this.effectiveFortune * 0.075F, this.worldObj.rand) else stack

  private def hasDowsing: Boolean =
    this.pickaxe.getItem.isInstanceOf[ItemElementalPickaxe] || (this.focus match {
      case null => false
      case stack => stack.getItem.asInstanceOf[ItemFocusBasic].isUpgradedWith(stack, ItemFocusExcavation.dowsing)
    })

  private def collectNearbyDrops(drops: ArrayList[ItemStack]): Unit = {
    val entities = this.worldObj.getEntitiesWithinAABB(classOf[EntityItem], AxisAlignedBB.getBoundingBox(this.targetX, this.targetY, this.targetZ, this.targetX + 1, this.targetY + 1, this.targetZ + 1).expand(1.0D, 1.0D, 1.0D))
    var index = 0
    while (index < entities.size()) {
      val entity = entities.get(index).asInstanceOf[EntityItem]
      drops.add(entity.getEntityItem.copy())
      entity.setDead()
      index += 1
    }
  }

  private def outputDrop(stack: ItemStack): Unit = {
    if (stack == null || stack.stackSize <= 0) return
    val outputSide = this.orientation.getOpposite
    val tile = this.worldObj.getTileEntity(this.xCoord + outputSide.offsetX, this.yCoord + outputSide.offsetY, this.zCoord + outputSide.offsetZ)
    val remainder = tile match {
      case inventory: IInventory => InventoryUtils.placeItemStackIntoInventory(stack, inventory, this.orientation.ordinal(), true)
      case _ => stack
    }
    if (remainder != null && remainder.stackSize > 0) {
      val entity = new EntityItem(this.worldObj, this.xCoord + 0.5D + outputSide.offsetX * 0.66D, this.yCoord + 0.5D + outputSide.offsetY * 0.66D, this.zCoord + 0.5D + outputSide.offsetZ * 0.66D, remainder.copy())
      entity.motionX = outputSide.offsetX * 0.075D
      entity.motionY = outputSide.offsetY * 0.075D + 0.025D
      entity.motionZ = outputSide.offsetZ * 0.075D
      this.worldObj.spawnEntityInWorld(entity)
    }
  }

  private def getFakePlayer: FakePlayer = {
    if (this.ownerFakePlayer == null && this.ownerId != null && this.worldObj.isInstanceOf[WorldServer]) {
      this.ownerFakePlayer = FakePlayerFactory.get(
        this.worldObj.asInstanceOf[WorldServer],
        new GameProfile(this.ownerId, this.ownerName)
      )
    }
    this.ownerFakePlayer
  }
}

object TileAdvancedArcaneBore {
  private final val minWidth = 3
  private final val maxWidth = 31
  private final val capacity = 64
  private final val maxDepth = 128
  private final val defaultDepth = 64
  private final val completionProbeInterval = 10
  private final val completionSearchLimit = 1440
  private final val searchStepsPerTick = 64

  private def normalizeWidth(requested: Int): Int = {
    val bounded = math.max(this.minWidth, math.min(this.maxWidth, requested))
    if ((bounded & 1) == 0) bounded - 1 else bounded
  }
}
