package dev.dediamondpro.polyblock.map

import dev.dediamondpro.polyblock.utils.SBInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.Files

object SkyblockMap {
    /**
     * format : <world id, <zone id, island data>>
     * Example of separate worlds: main (hub, ect...), winter (jerry's workshop)
     */
    var worlds = mutableMapOf<String, MutableMap<String, Island>>()
    var zoneToWorld = mutableMapOf<String, MutableMap<String, Island>>()

    fun getCurrentIsland(): Island? {
        return zoneToWorld[SBInfo.zone]?.get(SBInfo.zone)
    }

    fun currentIslandAvailable(): Boolean {
        return currentWorldAvailable() && getCurrentWorld()!!.containsKey(SBInfo.zone)
    }

    fun getCurrentWorld(): MutableMap<String, Island>? {
        return zoneToWorld[SBInfo.zone]
    }

    fun currentWorldAvailable(): Boolean {
        return zoneToWorld.containsKey(SBInfo.zone)
    }

    fun isZoneInWorld(zone: String): Boolean {
        return currentWorldAvailable() && getCurrentWorld()!!.containsKey(zone)
    }

    fun getZoneByIsland(island: Island): String? {
        for (world in worlds.values) {
            if (!world.containsValue(island)) continue
            for ((zone, island2) in world) {
                if (island == island2) return zone
            }
        }
        return null
    }

    fun initialize(file: File): Boolean {
        try {
            Files.newInputStream(file.toPath()).use {
                worlds = Json { ignoreUnknownKeys = true }.decodeFromStream(it)
                for (world in worlds.values) {
                    for (zone in world.keys) {
                        zoneToWorld[zone] = world
                    }
                }
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}