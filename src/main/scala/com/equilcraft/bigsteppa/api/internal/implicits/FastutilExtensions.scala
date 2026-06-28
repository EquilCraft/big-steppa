package com.equilcraft.bigsteppa.api.internal.implicits

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.util.ChunkCoordinates

import java.util.function.{IntFunction, LongFunction}

object FastutilExtensions { // generics? no.
  @inline private final val chunkListFactory = new LongFunction[ObjectArrayList[ChunkCoordinates]] {
    override def apply(value: Long): ObjectArrayList[ChunkCoordinates] =
      new ObjectArrayList[ChunkCoordinates]()
  }

  @inline private final val nestedChunkMapFactory = new IntFunction[Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]] {
    override def apply(value: Int): Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]] =
      new Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]()
  }

  implicit class ChunkListMapOps(private val map: Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]) extends AnyVal {
    def getOrCreate(key: Long): ObjectArrayList[ChunkCoordinates] =
      map.computeIfAbsent(key, chunkListFactory)
  }

  implicit class NestedChunkMapOps(private val map: Int2ObjectOpenHashMap[Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]]]) extends AnyVal {
    def getOrCreate(key: Int): Long2ObjectOpenHashMap[ObjectArrayList[ChunkCoordinates]] =
      map.computeIfAbsent(key, nestedChunkMapFactory)
  }
}
