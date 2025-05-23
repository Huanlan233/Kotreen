package top.htext.kotreen.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.Vec2ArgumentType
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.command.suggestion.ActionListSuggestionProvider
import top.htext.kotreen.command.suggestion.ArrangementSuggestionProvider
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.cache.ArrangementCache

object ArrangeCommand {
    private lateinit var arrangementCache: ArrangementCache
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = literal("arrange")
            .then(argument("arrange", StringArgumentType.word())
                .suggests(ArrangementSuggestionProvider())
                .then(literal("create")
                    .executes(ArrangeCommand::createArrangement)
                )
                .then(literal("remove")
                    .executes(ArrangeCommand::removeArrangement)
                )
                .then(literal("spawn")
                    .executes(ArrangeCommand::spawnArrangement)
                )
                .then(literal("kill")
                    .executes(ArrangeCommand::killArrangement)
                )
                .then(literal("action")
                    .executes(ArrangeCommand::actionArrangement)
                )
                .then(literal("stop")
                    .executes(ArrangeCommand::stopArrangement)
                )
                .then(literal("modify")
                    .then(literal("description")
                        .then(argument("description", StringArgumentType.greedyString())
                            .executes(ArrangeCommand::modifyDescription)
                        )
                    )
                    .then(literal("action")
                        .then(literal("append")
                            .then(argument("action", StringArgumentType.string())
                                .executes(ArrangeCommand::modifyActionAppend)
                            )
                        )
                        .then(literal("prepare")
                            .then(argument("action", StringArgumentType.string())
                                .executes(ArrangeCommand::modifyActionPrepare)
                            )
                        )
                        .then(literal("after")
                            .then(argument("former", IntegerArgumentType.integer(0))
                                .suggests(ActionListSuggestionProvider())
                                .then(argument("latter", StringArgumentType.string())
                                    .executes(ArrangeCommand::modifyActionAfter)
                                )
                            )
                        )
                        .then(literal("remove")
                            .then(argument("index", IntegerArgumentType.integer(0))
                                .suggests(ActionListSuggestionProvider())
                                .executes(ArrangeCommand::modifyActionRemove)
                            )
                        )
                        .then(literal("overwrite")
                            .then(argument("index", IntegerArgumentType.integer(0))
                                .suggests(ActionListSuggestionProvider())
                                .then(argument("action", StringArgumentType.string())
                                    .executes(ArrangeCommand::modifyActionOverwrite)
                                )
                            )
                        )
                        .then(literal("clear")
                            .executes(ArrangeCommand::modifyActionsClear)
                        )
                    )
                    .then(literal("position")
                        .then(literal("here")
                            .executes(ArrangeCommand::modifyPositionHere)
                        )
                        .then(literal("at")
                            .then(argument("position", Vec3ArgumentType.vec3())
                                .executes(ArrangeCommand::modifyPositionAt)
                            )
                        )
                    )
                    .then(literal("rotation")
                        .then(literal("here")
                            .executes(ArrangeCommand::modifyRotationHere)
                        )
                        .then(literal("at")
                            .then(argument("rotation", Vec2ArgumentType.vec2())
                                .executes(ArrangeCommand::modifyRotationAt)
                            )
                        )
                    )
                )
            )
        dispatcher.register(command)
    }

    fun cacheInit() {
        arrangementCache = ArrangementCache.getInstance()
    }

    private fun createArrangement(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val desc = "There is no description."
        val pos = player.pos
        val rot = player.rotationClient
        val dimension = player.world.registryKey.value

        if (arrangementCache.getArrangement(name) != null) {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.existed"))
            return 0
        }

        val arrangement = Arrangement(name, desc, pos, rot, dimension, ArrayList())
        arrangementCache.createArrangement(arrangement)
        return 1
    }

    private fun removeArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        if (arrangementCache.removeArrangement(name)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
        return 0
    }

    private fun spawnArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        if (arrangement.spawn(context.source.server)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.online"))
        return 0
    }

    private fun killArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        if (arrangement.kill(context.source.server)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.offline"))
        return 0
    }

    private fun actionArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        if (arrangement.action(context.source.server)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.action"))
        return 0
    }

    private fun stopArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        if (arrangement.stop(context.source.server)) return 1
        context.source.sendError(Text.translatable("kotreen.command.failure.action"))
        return 0
    }

    private fun modifyDescription(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val description = StringArgumentType.getString(context, "description")
        arrangement.desc = description
        return 1
    }

    private fun modifyActionAppend(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val action = StringArgumentType.getString(context, "action")
        arrangement.actions.add(action)
        return 1
    }

    private fun modifyActionPrepare(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val action = StringArgumentType.getString(context, "action")
        arrangement.actions.add(0, action)
        return 1
    }

    private fun modifyActionAfter(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val former = IntegerArgumentType.getInteger(context, "former")
        val latter = StringArgumentType.getString(context, "latter")
        arrangement.actions.add(former + 1, latter)
        return 1
    }

    private fun modifyActionRemove(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val index = IntegerArgumentType.getInteger(context, "index")
        arrangement.actions.removeAt(index)
        return 1
    }

    private fun modifyActionOverwrite(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val index = IntegerArgumentType.getInteger(context, "index")
        val action = StringArgumentType.getString(context, "action")
        arrangement.actions.removeAt(index)
        arrangement.actions.add(action)
        return 1
    }

    private fun modifyActionsClear(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        arrangement.actions.removeAll(arrangement.actions.toSet())
        return 1
    }

    private fun modifyPositionHere(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        arrangement.pos = player.pos
        return 1
    }

    private fun modifyPositionAt(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val pos = Vec3ArgumentType.getVec3(context, "position")
        arrangement.pos = pos
        return 1
    }

    private fun modifyRotationHere(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        arrangement.rot = player.rotationClient
        return 1
    }

    private fun modifyRotationAt(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = arrangementCache.getArrangement(name) ?: run {
            context.source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
            return 0
        }
        val rot = Vec2ArgumentType.getVec2(context, "rotation")
        arrangement.rot = rot
        return 1
    }
}