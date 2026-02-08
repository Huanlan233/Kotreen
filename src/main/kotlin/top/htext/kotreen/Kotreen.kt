package top.htext.kotreen

import carpet.CarpetExtension
import carpet.CarpetServer
import carpet.settings.SettingsManager
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.FabricLoader
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory
import top.htext.kotreen.command.ArrangeCommand
import top.htext.kotreen.command.SeriesCommand
import top.htext.kotreen.config.cache.ArrangementCache
import top.htext.kotreen.config.cache.SeriesCache

@Suppress("removal")
object Kotreen : ModInitializer, CarpetExtension {
	private const val MOD_ID = "kotreen"
	private val VERSION = FabricLoader.INSTANCE.getModContainer(MOD_ID).map { it.metadata.version.friendlyString }.orElse("unknown")

	private val logger = LoggerFactory.getLogger(MOD_ID)
	private val settingsManager = SettingsManager(VERSION, MOD_ID, "Kotreen")

	override fun onInitialize() {
		logger.info("Kotreen Initialized.")
		CarpetServer.manageExtension(this)
		settingsManager.parseSettingsClass(KotreenSetting::class.java)
	}

	override fun onServerLoaded(server: MinecraftServer) {
		logger.info("Cache Loaded.")
		ArrangementCache.load(server)
		SeriesCache.load(server)
	}

	override fun onServerClosed(server: MinecraftServer) {
		logger.info("Cache Saved.")
		ArrangementCache.save()
		SeriesCache.save()
	}

	override fun onGameStarted() {

	}

	override fun extensionSettingsManager(): SettingsManager {
		return settingsManager
	}

	override fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>, commandBuildContext: CommandRegistryAccess) {
		ArrangeCommand.register(dispatcher)
		SeriesCommand.register(dispatcher)
	}
}