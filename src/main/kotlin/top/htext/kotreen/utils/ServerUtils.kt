package top.htext.kotreen.utils

import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import top.htext.kotreen.Kotreen.LOGGER
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import kotlin.io.path.notExists

object ServerUtils {
    fun getHashSetFile(server: MinecraftServer, file: String): File {
        val configPath = server.getSavePath(WorldSavePath.ROOT).resolve("kotreen")
        val filePath = configPath.resolve("$file.json")

        if (configPath.notExists()) Files.createDirectories(configPath)
        if (filePath.notExists()) {
            LOGGER.warn("$filePath not exists and has been created.")
            Files.createFile(filePath)
            BufferedWriter(FileWriter(filePath.toFile())).use { it.write("[]") }
        }

        return filePath.toFile()
    }
}