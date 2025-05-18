package top.htext.kotreen.config.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.serialization.*
import top.htext.kotreen.utils.ServerUtils

object ArrangementCache {
    private val cache = HashSet<Arrangement>()
    private var dirty = false
    private val mapper = ObjectMapper().apply {
        findAndRegisterModules()
        enable(SerializationFeature.INDENT_OUTPUT)
        registerModule(
            SimpleModule()
                .addDeserializer(Vec3d::class.java, Vec3dDeserializer())
                .addSerializer(Vec3d::class.java, Vec3dSerializer())
                .addDeserializer(Vec2f::class.java, Vec2fDeserializer())
                .addSerializer(Vec2f::class.java, Vec2fSerializer())
                .addDeserializer(Identifier::class.java, IdentifierDeserializer())
                .addSerializer(Identifier::class.java, IdentifierSerializer())
        )
    }

    fun init(server: MinecraftServer) {
        val file = ServerUtils.getHashSetFile(server, "arrangement")
        cache.addAll(mapper.readValue(file, object : TypeReference<HashSet<Arrangement>>(){}))
    }

    fun save(server: MinecraftServer) {
        if (dirty) {
            val file = ServerUtils.getHashSetFile(server, "arrangement")
            mapper.writeValue(file, cache)
        }
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