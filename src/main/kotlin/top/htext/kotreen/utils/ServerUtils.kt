package top.htext.kotreen.utils

import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
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
            Files.createFile(filePath)
            BufferedWriter(FileWriter(filePath.toFile())).use { it.write("[]") }
        }

        return filePath.toFile()
    }
}