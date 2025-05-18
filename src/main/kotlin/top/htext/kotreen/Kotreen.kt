package top.htext.kotreen

import carpet.CarpetExtension
import carpet.CarpetServer
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory
import top.htext.kotreen.config.cache.ArrangementCache
import top.htext.kotreen.command.ArrangeCommand
import top.htext.kotreen.command.SeriesCommand
import top.htext.kotreen.config.cache.SeriesCache

object Kotreen : ModInitializer, CarpetExtension {
    private val logger = LoggerFactory.getLogger("kotreen")

	override fun onInitialize() {
		logger.info("Kotreen Initialized.")
		CarpetServer.manageExtension(this)
	}

	override fun onServerLoaded(server: MinecraftServer) {
		logger.info("Cache Initialized.")
		ArrangementCache.init(server)
		SeriesCache.init(server)
	}

	override fun onServerClosed(server: MinecraftServer) {
		logger.info("Cache Saved.")
		ArrangementCache.save(server)
		SeriesCache.save(server)
	}

	override fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>, commandBuildContext: CommandRegistryAccess) {
		ArrangeCommand.register(dispatcher)
		SeriesCommand.register(dispatcher)
	}
}