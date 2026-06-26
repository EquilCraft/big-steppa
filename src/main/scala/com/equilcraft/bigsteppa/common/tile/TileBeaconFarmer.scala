package com.equilcraft.bigsteppa.common.tile

import com.equilcraft.bigsteppa.api.BigFakePlayer
import com.equilcraft.bigsteppa.common.block.build.BlockStructure
import com.equilcraft.bigsteppa.common.block.update.{BlockDamageUpdate, BlockLootingUpdate}
import com.equilcraft.bigsteppa.common.entity.EntityDopplegangerSpawned
import com.equilcraft.bigsteppa.common.tile.TileBeaconFarmer._
import com.equilcraft.bigsteppa.implicits.ConversionJavaList.JavaListForeach
import com.mojang.authlib.GameProfile
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.server.FMLServerHandler
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.tileentity.{TileEntity, TileEntityBeacon}
import net.minecraft.util.{AxisAlignedBB, ChunkCoordinates, DamageSource}
import net.minecraft.world.{World, WorldServer}
import vazkii.botania.common.block.BlockPylon
import vazkii.botania.common.entity.{EntityDoppleganger, EntityDopplegangerHelper}
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.material.ItemManaResource

import java.util.UUID
import java.util.function.IntFunction
import scala.collection.mutable

class TileBeaconFarmer extends TileEntity with IInventory {
  private var ingot: ItemStack = null
  private var killing: Option[EntityDoppleganger] = None
  private var completedStructure: Boolean = false

  private var damageUpdate: Int = 1
  private var lootUpdate: Int = 0

  lazy val fakePlayer: BigFakePlayer =
    new BigFakePlayer(this.worldObj.asInstanceOf[WorldServer],
      new GameProfile(UUID.fromString(fakePlayerUUID), fakePlayerName))

  override def updateEntity(): Unit = {
    if (this.worldObj.isRemote) return

    if (this.worldObj.getTotalWorldTime % 100L == 0L) {
      this.applyEffects()
      this.completedStructure = this.checkStructure()
    }

    if (this.completedStructure && this.worldObj.getTotalWorldTime % 20L == 0L) {
      if (!this.killing.exists(_.isEntityAlive) && !this.spawnGaia()) return

      val aabb = AxisAlignedBB.getBoundingBox(this.xCoord - 12, this.yCoord - 2, this.zCoord - 12, this.xCoord + 12, this.yCoord + 6, this.zCoord + 12)
      this.worldObj.getEntitiesWithinAABB(classOf[EntityLivingBase], aabb).foreach {
        entity => {
          entity.attackEntityFrom(DamageSource.causePlayerDamage(fakePlayer), 20 * this.damageUpdate)
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

  private def checkStructure(): Boolean = {
    this.damageUpdate = 1
    this.lootUpdate = 0

    pylons.forall {
      case (x, y, z) =>
        this.worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z) match {
          case _: BlockPylon if this.worldObj.getBlockMetadata(this.xCoord + x, this.yCoord + y, this.zCoord + z) == 2 => true
          case _ => false
        }
    } && structure.forall {
      case (x, y, z) =>
        this.worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z) match {
          case _: BlockDamageUpdate => this.damageUpdate += 1; true
          case _: BlockLootingUpdate => this.lootUpdate += 1; true
          case _: BlockStructure => true
          case _ => false
        }
    }
  }

  private def applyEffects(): Unit = {
    val aabb = AxisAlignedBB
      .getBoundingBox(this.xCoord - radius, this.yCoord, this.zCoord - radius,
                      this.xCoord + radius, this.worldObj.getHeight, this.zCoord + radius)

    this.worldObj.getEntitiesWithinAABB(classOf[EntityPlayer], aabb).foreach {
      player: EntityPlayer => {
        for {
          group <- TileEntityBeacon.effectsList
          effect <- group
        } player.addPotionEffect(new PotionEffect(effect.id, 400, 1, true))
      }
    }
  }

  override def validate(): Unit = {
    super.validate()
    if (!this.worldObj.isRemote) {
      TileBeaconFarmer.add(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
    }
  }

  override def invalidate(): Unit = {
    super.invalidate()
    if (!this.worldObj.isRemote) {
      TileBeaconFarmer.remove(this.worldObj, this.xCoord, this.yCoord, this.zCoord)
    }
  }

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
}

object TileBeaconFarmer {
  final val fakePlayerUUID = "24b4fdb0-01c5-3732-8433-8fef2d2643a6"
  final val fakePlayerName = "[BeaconFarmer]"
  final val spawnTicks: Int = 100
  final val radius: Double = 50.0D

  final val pylons: List[(Int, Int, Int)] = List((4, 1, 4), (4, 1, -4), (-4, 1, 4), (-4, 1, -4))
  final val structure: List[(Int, Int, Int)] = List((6,1,1), (6,1,0), (6,1,-1), (5,1,3), (5,1,2), (5,1,-2), (5,1,-3), (3,1,5), (3,1,-5), (2,1,5), (2,1,-5), (1,1,6), (1,1,-6), (0,1,6), (0,1,-6), (-1,1,6), (-1,1,-6), (-2,1,5), (-2,1,-5), (-3,1,5), (-3,1,-5), (-5,1,3), (-5,1,2), (-5,1,-2), (-5,1,-3), (-6,1,1), (-6,1,0), (-6,1,-1))

  final val defaultRegistryList = new IntFunction[ObjectArrayList[ChunkCoordinates]] {
    override def apply(value: Int): ObjectArrayList[ChunkCoordinates] = new ObjectArrayList[ChunkCoordinates]()
  }

  val registry = new Int2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]()

  def add(world: World, x: Int, y: Int, z: Int): Unit =
    registry.computeIfAbsent(world.provider.dimensionId, defaultRegistryList)
      .add(new ChunkCoordinates(x, y, z))

  def remove(world: World, x: Int, y: Int, z: Int): Unit = {
    val dimId = world.provider.dimensionId
    if (registry.containsKey(dimId)) registry.get(dimId).remove(new ChunkCoordinates(x, y, z))
  }

  def getPositions(world: World): ObjectArrayList[ChunkCoordinates] =
    registry.getOrDefault(world.provider.dimensionId, new ObjectArrayList[ChunkCoordinates]())

  def findNearestTileWithRadius(world: World, startX: Int, startY: Int, startZ: Int, maxRadius: Double): ChunkCoordinates = {
    val positions = getPositions(world)

    if (positions == null || positions.isEmpty) {
      return null
    }

    var nearest: ChunkCoordinates = null
    val maxRadiusSq = maxRadius * maxRadius
    var minDistanceSq = maxRadiusSq

    val size = positions.size()
    var i = 0
    while (i < size) {
      val coords = positions.get(i)

      if (!(coords.posX == startX && coords.posY == startY && coords.posZ == startZ)) {
        val distSq = coords.getDistanceSquared(startX, startY, startZ)

        if (distSq < minDistanceSq) {
          minDistanceSq = distSq
          nearest = coords
        }
      }
      i += 1
    }

    nearest
  }
}
