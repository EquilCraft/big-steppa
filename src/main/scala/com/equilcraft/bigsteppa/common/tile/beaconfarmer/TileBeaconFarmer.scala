package com.equilcraft.bigsteppa.common.tile.beaconfarmer

import com.equilcraft.bigsteppa.api.BigFakePlayer
import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry
import com.equilcraft.bigsteppa.common.blocks.beaconfarmer.{BlockDamageUpdate, BlockLootingUpdate, BlockStructure}
import com.equilcraft.bigsteppa.common.tile.{MultiblockController, SpatialRegistered}
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer._
import com.equilcraft.bigsteppa.api.internal.implicits.ConversionJavaList.JavaListForeach
import com.equilcraft.bigsteppa.common.structure.beaconfarmer.BeaconFarmerStructure
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.mojang.authlib.GameProfile
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.{TileEntity, TileEntityBeacon}
import net.minecraft.util.{AxisAlignedBB, DamageSource}
import vazkii.botania.common.entity.{EntityDoppleganger, EntityDopplegangerHelper}
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.material.ItemManaResource

import java.util.UUID

class TileBeaconFarmer
    extends TileEntity
    with IInventory
    with MultiblockController[TileBeaconFarmer]
    with SpatialRegistered[TileBeaconFarmer] {

  private var ingot: ItemStack = null
  private var killing: Option[EntityDoppleganger] = None

  private var damageUpdate: Int = 1
  private var lootUpdate: Int = 0

  private[bigsteppa] def recordUpgrade(block: Block): Boolean = block match {
    case _: BlockDamageUpdate =>
      this.damageUpdate += 1
      true
    case _: BlockLootingUpdate =>
      this.lootUpdate += 1
      true
    case _: BlockStructure => true
    case _ => false
  }

  lazy val fakePlayer: BigFakePlayer = BigFakePlayer
    .getFakePlayer(this.worldObj,
      new GameProfile(UUID.fromString(fakePlayerUUID), fakePlayerName))


  override protected def structureDef: IStructureDefinition[TileBeaconFarmer] =
    BeaconFarmerStructure.structureDefinition
  override protected def mainPiece: String = "main"
  override protected def horizontalOffset: Int = 6
  override protected def verticalOffset: Int = 1
  override protected def depthOffset: Int = 6


  override protected def spatialRegistry: BlocksChaosStructureRegistry[TileBeaconFarmer] = TileBeaconFarmer.registry


  override protected def onPreStructureCheck(): Unit = {
    this.damageUpdate = 1
    this.lootUpdate = 0
  }


  override def updateEntity(): Unit = {
    if (this.worldObj.isRemote) return

    if (this.worldObj.getTotalWorldTime % 100L == 0L) {
      tickStructureCheck()
    }

    if (this.isStructureFormed && this.worldObj.getTotalWorldTime % 20L == 0L) {
      if (!this.killing.exists(_.isEntityAlive) && !this.spawnGaia()) return

      val aabb = AxisAlignedBB.getBoundingBox(this.xCoord - radiusDamage, this.yCoord - 1, this.zCoord - radiusDamage, this.xCoord + radiusDamage, this.yCoord + 6, this.zCoord + radiusDamage)
      this.worldObj.getEntitiesWithinAABB(classOf[EntityLivingBase], aabb).foreach {
        entity => {
          entity.attackEntityFrom(DamageSource.causePlayerDamage(fakePlayer), 25 * this.damageUpdate)
        }
      }

      if (!this.killing.exists(_.isEntityAlive))
        this.killing.get.entityDropItem(new ItemStack(ModItems.manaResource, 3 * this.lootUpdate, 5), 1.0F)
    }
  }

  private def spawnGaia(): Boolean = {
    val itemStack = this.decrStackSize(1)

    if (itemStack == null) return false

    val doppleganger = new EntityDoppleganger(this.worldObj)
    doppleganger.setPosition(this.xCoord + 0.5, this.yCoord + 3, this.zCoord + 0.5)
    doppleganger.setInvulTime(spawnTicks)
    doppleganger.setHealth(1F)
    doppleganger.setSource(this.xCoord, this.yCoord, this.zCoord)
    doppleganger.setMobSpawnTicks(EntityDoppleganger.MOB_SPAWN_TICKS)
    doppleganger.setHardMode(itemStack.getItemDamage == 14)

    doppleganger.setPlayerCount(1)
    doppleganger.getAttributeMap.getAttributeInstance(SharedMonsterAttributes.maxHealth).setBaseValue(800)

    EntityDopplegangerHelper.setFakePlayer(doppleganger)

    this.worldObj.playSoundAtEntity(doppleganger, "mob.enderdragon.growl", 10F, 0.1F)
    this.worldObj.spawnEntityInWorld(doppleganger)

    this.killing = Some(doppleganger)
    true
  }

  override def getStructureDescription(stackSize: ItemStack): Array[String] =
    Array("Gaia pylons and structure blocks must be one block above the controller")


  override def getStackInSlot(slot: Int): ItemStack = this.ingot

  private def decrStackSize(count: Int): ItemStack = this.decrStackSize(0, count)

  override def decrStackSize(slot: Int, count: Int): ItemStack = {
    this.getStackInSlot(slot) match {
      case stack: ItemStack if stack.stackSize > count =>
        stack.stackSize -= count
        new ItemStack(stack.getItem, count, stack.getItemDamage)
      case stack: ItemStack if stack.stackSize == count =>
        this.setInventorySlotContents(slot, null)
        stack
      case _ => null
    }
  }

  override def getStackInSlotOnClosing(slot: Int): ItemStack = {
    val stack: ItemStack = this.getStackInSlot(slot)
    this.setInventorySlotContents(slot, null)
    stack
  }

  override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = this.ingot = stack

  override def getInventoryName: String = "container.beaconfarmer"

  override def hasCustomInventoryName: Boolean = true

  override def getInventoryStackLimit: Int = 1

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean =
    stack.getItem.isInstanceOf[ItemManaResource] &&
      (stack.getItemDamage == 4 || stack.getItemDamage == 14)

  override def getSizeInventory: Int = 1

  override def isUseableByPlayer(player: EntityPlayer): Boolean =
    if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) ne this) false
    else player.getDistanceSq(this.xCoord.toDouble + 0.5D, this.yCoord.toDouble + 0.5D, this.zCoord.toDouble + 0.5D) <= 64.0D

  override def openInventory(): Unit = {}

  override def closeInventory(): Unit = {}

  override def validate(): Unit = {
    super.validate()
    spatialValidate()
  }

  override def invalidate(): Unit = {
    super.invalidate()
    spatialInvalidate()
  }
}

object TileBeaconFarmer {
  val registry = new BlocksChaosStructureRegistry[TileBeaconFarmer]()
  final val fakePlayerUUID = "24b4fdb0-01c5-3732-8433-8fef2d2643a6"
  final val fakePlayerName = "[BeaconFarmer]"
  private final val spawnTicks = 100
  private final val radiusDamage = 15

}
