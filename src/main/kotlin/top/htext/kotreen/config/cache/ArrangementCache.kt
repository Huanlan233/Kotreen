package top.htext.kotreen.config.cache

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.Kotreen.LOGGER
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.utils.ServerUtils
import java.io.File

object ArrangementCache {
    private val cache = HashSet<Arrangement>()
    private var dirty = false
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    private lateinit var file: File


    fun load(server: MinecraftServer) {
        this.file = ServerUtils.getHashSetFile(server, "arrangement")
        LOGGER.info("Arrangement Cache Loaded From ${file.toPath()}")
        cache.removeAll(cache)

        val typeToken = object : TypeToken<HashSet<Arrangement>>(){}.type
        val element = JsonParser.parseReader(file.reader())
        if (!element.isJsonArray || element.isJsonNull) {
            cache.addAll(HashSet())
            LOGGER.warn("Failed to read cache file ${file.toPath()} and has written new empty set.")
        } else {
            cache.addAll(gson.fromJson<HashSet<Arrangement>>(file.reader(), typeToken))
        }
    }

    fun save() {
        if (!dirty) return
        LOGGER.info("Arrangement Cache Saved.")
        file.writer().use {
            gson.toJson(cache, it)
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

    fun getArrangement(name: String, source: ServerCommandSource): Arrangement? {
        val result = getArrangement(name)
        if (result == null) {
            source.sendError(Text.translatable("kotreen.command.failure.arrangement.null", name))
            return null
        }
        return result
    }

    fun createArrangement(arrangement: Arrangement, source: ServerCommandSource): Boolean {
        val isCreated = createArrangement(arrangement)
        if (!isCreated) source.sendError(Text.translatable("kotreen.command.failure.arrangement.existed", arrangement.name))
        return isCreated
    }

    fun removeArrangement(name: String, source: ServerCommandSource): Boolean {
        val isRemoved = removeArrangement(name)
        if (!isRemoved) source.sendError(Text.translatable("kotreen.command.failure.arrangement.null", name))
        return isRemoved
    }
}