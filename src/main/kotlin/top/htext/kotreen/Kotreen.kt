package top.htext.kotreen

import carpet.CarpetExtension
import carpet.CarpetServer
import carpet.api.settings.SettingsManager
import carpet.utils.Translations
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.FabricLoader
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.htext.kotreen.command.ArrangeCommand
import top.htext.kotreen.command.SeriesCommand
import top.htext.kotreen.config.cache.ArrangementCache
import top.htext.kotreen.config.cache.SeriesCache

object Kotreen : ModInitializer, CarpetExtension {
	const val MOD_ID = "kotreen"
	val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
	val VERSION: String = FabricLoader.INSTANCE.getModContainer(MOD_ID).map { it.metadata.version.friendlyString }.orElse("unknown")
	val settingsManager = SettingsManager(VERSION, MOD_ID, "Kotreen")

	init {
		CarpetServer.manageExtension(this)
	}

	override fun onInitialize() {
		LOGGER.info("Kotreen Initialized.")
	}

	override fun onServerLoaded(server: MinecraftServer) {
		LOGGER.info("Cache Loaded.")
		ArrangementCache.load(server)
		SeriesCache.load(server)
	}

	override fun onServerClosed(server: MinecraftServer) {
		LOGGER.info("Cache Saved.")
		ArrangementCache.save()
		SeriesCache.save()
	}

	override fun onGameStarted() {
		settingsManager.parseSettingsClass(KotreenSetting::class.java)
	}

	override fun extensionSettingsManager(): SettingsManager {
		return settingsManager
	}

	override fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>, commandBuildContext: CommandRegistryAccess) {
		ArrangeCommand.register(dispatcher)
		SeriesCommand.register(dispatcher)
	}

	override fun canHasTranslations(lang: String): Map<String, String> {
		return Translations.getTranslationFromResourcePath("assets/${MOD_ID}/lang/$lang.json")
	}
}