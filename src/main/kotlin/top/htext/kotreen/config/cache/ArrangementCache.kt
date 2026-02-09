package top.htext.kotreen.config.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.minecraft.server.MinecraftServer
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.utils.ServerUtils
import java.io.File

object ArrangementCache {
    private val cache = HashSet<Arrangement>()
    private var dirty = false
    private val mapper = ObjectMapper().apply {
        findAndRegisterModules()
        enable(SerializationFeature.INDENT_OUTPUT)
    }
    private lateinit var file: File


    fun load(server: MinecraftServer) {
        this.file = ServerUtils.getHashSetFile(server, "arrangement")
        cache.removeAll(cache)
        cache.addAll(mapper.readValue(file, object : TypeReference<HashSet<Arrangement>>(){}))
    }

    fun save() {
        if (!dirty) return
        mapper.writeValue(file, cache)
    }

    fun getCache(): HashSet<Arrangement> {
        return cache
    }

    fun getArrangement(name: String): Arrangement? {
        try { return cache.first { name == it.name } }
        catch (_: NoSuchElementException) { }
        return null
    }

    fun createArrangement(arrangement: Arrangement): Boolean {
        dirty = true
        return cache.add(arrangement)
    }

    fun removeArrangement(name: String): Boolean {
        dirty = true
        return cache.removeIf { it.name == name }
    }
}