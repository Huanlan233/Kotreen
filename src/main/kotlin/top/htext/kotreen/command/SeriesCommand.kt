package top.htext.kotreen.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.command.suggestion.ArrangementSuggestionProvider
import top.htext.kotreen.command.suggestion.SeriesArrangementSuggestionProvider
import top.htext.kotreen.command.suggestion.SeriesSuggestionProvider
import top.htext.kotreen.config.Series
import top.htext.kotreen.config.cache.ArrangementCache
import top.htext.kotreen.config.cache.SeriesCache

object SeriesCommand {
    private lateinit var seriesCache: SeriesCache
    private lateinit var arrangementCache: ArrangementCache
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = literal("series")
            .then(argument("series", StringArgumentType.word())
                .suggests(SeriesSuggestionProvider())
                .then(literal("create")
                    .executes(SeriesCommand::createSeries)
                )
                .then(literal("remove")
                    .executes(SeriesCommand::removeSeries)
                )
                .then(literal("spawn")
                    .executes(SeriesCommand::spawnSeries)
                )
                .then(literal("kill")
                    .executes(SeriesCommand::killSeries)
                )
                .then(literal("action")
                    .executes(SeriesCommand::actionSeries)
                )
                .then(literal("stop")
                    .executes(SeriesCommand::stopSeries)
                )
                .then(literal("modify")
                    .then(literal("description")
                        .then(argument("description", StringArgumentType.greedyString())
                            .executes(SeriesCommand::modifyDescription)
                        )
                    )
                    .then(literal("arrange")
                        .then(literal("add")
                            .then(argument("arrangement", StringArgumentType.word())
                                .suggests(ArrangementSuggestionProvider())
                                .executes(SeriesCommand::modifyArrangementAdd)
                            )
                        )
                        .then(literal("remove")
                            .then(argument("arrangement", StringArgumentType.word())
                                .suggests(SeriesArrangementSuggestionProvider())
                                .executes(SeriesCommand::modifyArrangementRemove)
                            )
                        )
                        .then(literal("clear")
                            .executes(SeriesCommand::modifyArrangementsClear)
                        )
                    )
                )
            )
        dispatcher.register(command)
    }

    fun cacheInit() {
        seriesCache = SeriesCache.getInstance()
        arrangementCache = ArrangementCache.getInstance()
    }

    private fun createSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val desc = "There is no description."
        val series = Series(name, desc, HashSet())
        if (seriesCache.createSeries(series)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.series.existed"))
        return 0
    }

    private fun removeSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        if (seriesCache.removeSeries(name)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
        return 0
    }

    private fun spawnSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.spawn(context.source.server)
    }

    private fun killSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.kill(context.source.server)
    }

    private fun actionSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.action(context.source.server)
    }

    private fun stopSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.stop(context.source.server)
    }

    private fun modifyDescription(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val description = StringArgumentType.getString(context, "description")
        series.desc = description
        return 1
    }

    private fun modifyArrangementAdd(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val arrangementName = StringArgumentType.getString(context, "arrangement")
        val arrangement = arrangementCache.getArrangement(arrangementName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        series.arrangements.add(arrangement)
        return 1
    }

    private fun modifyArrangementRemove(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val arrangementName = StringArgumentType.getString(context, "arrangement")
        val arrangement = arrangementCache.getArrangement(arrangementName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        series.arrangements.remove(arrangement)
        return 1
    }

    private fun modifyArrangementsClear(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = seriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        series.arrangements.removeAll(series.arrangements)
        return 1
    }
}