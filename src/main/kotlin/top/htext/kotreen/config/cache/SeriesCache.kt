package top.htext.kotreen.config.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import net.minecraft.server.MinecraftServer
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.Series
import top.htext.kotreen.serialization.ArrangementDeserializer
import top.htext.kotreen.serialization.ArrangementSerializer
import top.htext.kotreen.utils.ServerUtils

object SeriesCache {
    private val cache = HashSet<Series>()
    private var dirty = false
    private val mapper = ObjectMapper().apply {
        findAndRegisterModules()
        enable(SerializationFeature.INDENT_OUTPUT)
        registerModules(
            SimpleModule()
                .addSerializer(Arrangement::class.java, ArrangementSerializer())
                .addDeserializer(Arrangement::class.java, ArrangementDeserializer())
        )
    }

    fun init(server: MinecraftServer) {
        val file = ServerUtils.getHashSetFile(server, "succession")
        cache.addAll(mapper.readValue(file, object : TypeReference<HashSet<Series>>(){}))
    }

    fun save(server: MinecraftServer) {
        if (dirty) {
            val file = ServerUtils.getHashSetFile(server, "succession")
            mapper.writeValue(file, cache)
        }
    }

    fun getCache(): HashSet<Series> {
        return cache
    }

    fun getSeries(name: String): Series? {
        try { return cache.first{ it.name == name } }
        catch (_: NoSuchElementException) { }
        return null
    }

    fun createSeries(series: Series): Boolean {
        dirty = true
        return cache.add(series)
    }

    fun removeSeries(name: String): Boolean {
        dirty = true
        return cache.removeIf { it.name == name }
    }
}