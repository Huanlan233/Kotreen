package top.htext.kotreen.command

import carpet.utils.CommandHelper
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.KotreenSetting
import top.htext.kotreen.command.suggestion.ArrangementListSuggestionProvider
import top.htext.kotreen.command.suggestion.ArrangementSuggestionProvider
import top.htext.kotreen.command.suggestion.SeriesArrangementSuggestionProvider
import top.htext.kotreen.command.suggestion.SeriesSuggestionProvider
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.Series
import top.htext.kotreen.config.cache.ArrangementCache
import top.htext.kotreen.config.cache.SeriesCache

object SeriesCommand {
    private fun <S : ServerCommandSource, T : ArgumentBuilder<S, T>> T.hasPermission(permission: Any): T {
        return this.requires { CommandHelper.canUseCommand(it, permission) }
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command: LiteralArgumentBuilder<ServerCommandSource> = literal("series")
            .then(argument("series", StringArgumentType.word())
                .hasPermission(KotreenSetting.seriesPermission)
                .suggests(SeriesSuggestionProvider())
                .then(literal("create")
                    .hasPermission(KotreenSetting.seriesCreatePermission)
                    .executes(SeriesCommand::createSeries)
                    .then(argument("arrangements", StringArgumentType.greedyString())
                        .suggests(ArrangementListSuggestionProvider())
                        .executes(SeriesCommand::createSeriesWithArrangements)
                    )
                )
                .then(literal("remove")
                    .hasPermission(KotreenSetting.seriesRemovePermission)
                    .executes(SeriesCommand::removeSeries)
                )
                .then(literal("spawn")
                    .hasPermission(KotreenSetting.seriesSpawnPermission)
                    .executes(SeriesCommand::spawnSeries)
                )
                .then(literal("kill")
                    .hasPermission(KotreenSetting.seriesKillPermission)
                    .executes(SeriesCommand::killSeries)
                )
                .then(literal("action")
                    .hasPermission(KotreenSetting.seriesActionPermission)
                    .executes(SeriesCommand::actionSeries)
                )
                .then(literal("stop")
                    .hasPermission(KotreenSetting.seriesStopPermission)
                    .executes(SeriesCommand::stopSeries)
                )
                .then(literal("modify")
                    .hasPermission(KotreenSetting.seriesModifyPermission)
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

    private fun createSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val desc = "There is no description."
        val series = Series(name, desc, HashSet())
        if (SeriesCache.createSeries(series)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.series.existed"))
        return 0
    }

    private fun createSeriesWithArrangements(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val desc = "There is no description."
        val arrangementNames = StringArgumentType.getString(context, "arrangements").split(" ").toHashSet()
        val arrangements = HashSet<Arrangement>()
        arrangementNames.forEach {
            ArrangementCache.getArrangement(it)?.let { e -> arrangements.add(e) }
        }

        val series = Series(name, desc, arrangements)
        if (SeriesCache.createSeries(series)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.series.existed"))
        return 0
    }

    private fun removeSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        if (SeriesCache.removeSeries(name)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
        return 0
    }

    private fun spawnSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.spawn(context.source.server)
    }

    private fun killSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.kill(context.source.server)
    }

    private fun actionSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.action(context.source.server)
    }

    private fun stopSeries(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        return series.stop(context.source.server)
    }

    private fun modifyDescription(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val description = StringArgumentType.getString(context, "description")
        series.desc = description
        return 1
    }

    private fun modifyArrangementAdd(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val arrangementName = StringArgumentType.getString(context, "arrangement")
        val arrangement = ArrangementCache.getArrangement(arrangementName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        series.arrangements.add(arrangement)
        return 1
    }

    private fun modifyArrangementRemove(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        val arrangementName = StringArgumentType.getString(context, "arrangement")
        val arrangement = ArrangementCache.getArrangement(arrangementName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        series.arrangements.remove(arrangement)
        return 1
    }

    private fun modifyArrangementsClear(context: CommandContext<ServerCommandSource>): Int {
        val seriesName = StringArgumentType.getString(context, "series")
        val series = SeriesCache.getSeries(seriesName) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.series.null"))
            return 0
        }
        series.arrangements.removeAll(series.arrangements)
        return 1
    }
}