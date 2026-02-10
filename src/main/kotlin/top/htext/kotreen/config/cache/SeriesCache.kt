package top.htext.kotreen.config.cache

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.Kotreen.LOGGER
import top.htext.kotreen.config.Series
import top.htext.kotreen.utils.ServerUtils
import java.io.File

object SeriesCache {
    private val cache = HashSet<Series>()
    private var dirty = false
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    private lateinit var file: File

    fun load(server: MinecraftServer) {
        this.file = ServerUtils.getHashSetFile(server, "succession")
        LOGGER.info("Series Cache Loaded From ${file.toPath()}")
        cache.removeAll(cache)

        val typeToken = object : TypeToken<HashSet<Series>>() {}.type
        val element = JsonParser.parseReader(file.reader())
        if (!element.isJsonArray || element.isJsonNull) {
            cache.addAll(HashSet())
            LOGGER.warn("Failed to read cache file ${file.toPath()} and has written new empty set.")
        } else {
            cache.addAll(gson.fromJson<HashSet<Series>>(file.reader(), typeToken))
        }
    }

    fun save() {
        if (!dirty) return
        LOGGER.info("Series Cache Saved.")
        file.writer().use {
            gson.toJson(cache, it)
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

    fun getSeries(name: String, source: ServerCommandSource): Series? {
        val result = getSeries(name)
        if (result == null) {
            source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return null
        }
        return result
    }

    fun createSeries(series: Series, source: ServerCommandSource): Boolean {
        val isCreated = createSeries(series)
        if (!isCreated) source.sendError(Text.translatable("kotreen.command.failure.series.existed"))
        return isCreated
    }

    fun removeSeries(name: String, source: ServerCommandSource): Boolean {
        val isRemoved = removeSeries(name)
        if (!isRemoved) source.sendError(Text.translatable("kotreen.command.failure.series.null"))
        return isRemoved
    }
}