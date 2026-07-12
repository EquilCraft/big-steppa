package com.equilcraft.bigsteppa.api.internal

import com.equilcraft.bigsteppa.api.internal.implicits.FastutilExtensions._
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.{ChunkCoordIntPair, World}

class BlocksChaosStructureRegistry[A] {
  private lazy val registry = new Int2ObjectOpenHashMap[Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]]()

  def add(world: World, x: Int, y: Int, z: Int): Unit = {
    val dim = world.provider.dimensionId
    val chunkX = x >> 4
    val chunkZ = z >> 4
    val chunkKey = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ)

    val dimMap = this.registry.getOrCreate(dim)
    val list = dimMap.getOrCreate(chunkKey)

    val coords = new ChunkCoordinates(x, y, z)
    if (!list.contains(coords)) list.add(coords)
  }

  def remove(world: World, x: Int, y: Int, z: Int): Unit = {
    val chunkKey = ChunkCoordIntPair.chunkXZ2Int(x >> 4, z >> 4)

    val dimMap = this.registry.get(world.provider.dimensionId)
    if (dimMap != null) {
      val list = dimMap.get(chunkKey)
      if (list != null) {
        list.remove(new ChunkCoordinates(x, y, z))
        if (list.isEmpty) dimMap.remove(chunkKey)
      }
    }
  }

  def getTilesInChunk(dim: Int, chunkX: Int, chunkZ: Int): ObjectArrayList[ChunkCoordinates] = {
    val dimMap = this.registry.get(dim)
    if (dimMap == null) return null
    dimMap.get(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ))
  }

  def findNearestTileWithRadius(world: World, startX: Int, startY: Int, startZ: Int, maxRadius: Int): ChunkCoordinates = {
    val dim = world.provider.dimensionId

    val minChunkX = (startX - maxRadius) >> 4
    val maxChunkX = (startX + maxRadius) >> 4
    val minChunkZ = (startZ - maxRadius) >> 4
    val maxChunkZ = (startZ + maxRadius) >> 4

    var nearest: ChunkCoordinates = null
    var minDistanceSq = (maxRadius * maxRadius).toFloat

    var cx = minChunkX
    while (cx <= maxChunkX) {
      var cz = minChunkZ
      while (cz <= maxChunkZ) {

        val positions = this.getTilesInChunk(dim, cx, cz)
        if (positions != null && !positions.isEmpty) {

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
        }
        cz += 1
      }
      cx += 1
    }
    nearest
  }

  def findAllTilesWithRadius(world: World, startX: Int, startY: Int, startZ: Int, maxRadius: Int): ObjectArrayList[A] = {
    val resultList = new ObjectArrayList[A]()

    val minChunkX = (startX - maxRadius) >> 4
    val maxChunkX = (startX + maxRadius) >> 4
    val minChunkZ = (startZ - maxRadius) >> 4
    val maxChunkZ = (startZ + maxRadius) >> 4

    val maxRadiusSq = maxRadius * maxRadius

    var cx = minChunkX
    while (cx <= maxChunkX) {
      var cz = minChunkZ
      while (cz <= maxChunkZ) {
        val positions = this.getTilesInChunk(world.provider.dimensionId, cx, cz)
        if (positions != null && !positions.isEmpty) {
          val size = positions.size()
          var i = 0
          while (i < size) {
            val coords = positions.get(i)

            if (!(coords.posX == startX && coords.posY == startY && coords.posZ == startZ)) {
              val distSq = coords.getDistanceSquared(startX, startY, startZ)

              if (distSq <= maxRadiusSq) {
                val tile = world.getTileEntity(coords.posX, coords.posY, coords.posZ)

                if (tile != null && tile.isInstanceOf[A]) {
                  resultList.add(tile.asInstanceOf[A])
                }
              }
            }
            i += 1
          }
        }
        cz += 1
      }
      cx += 1
    }

    resultList
  }
}
