package top.htext.kotreen.command

import carpet.utils.CommandHelper
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.Vec2ArgumentType
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import top.htext.kotreen.KotreenSetting
import top.htext.kotreen.command.suggestion.ActionListSuggestionProvider
import top.htext.kotreen.command.suggestion.ArrangementSuggestionProvider
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.cache.ArrangementCache

object ArrangeCommand {
    private fun <S : ServerCommandSource, T : ArgumentBuilder<S, T>> T.hasPermission(permission: Any): T {
        return this.requires { CommandHelper.canUseCommand(it, permission) }
    }

    private fun Vec3d.toList(): List<Double> {
        return listOf(this.x, this.y, this.z)
    }

    private fun Vec2f.toList(): List<Float> {
        return listOf(this.x, this.y)
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = literal("arrange")
            .then(argument("arrange", StringArgumentType.word())
                .suggests(ArrangementSuggestionProvider())
                .hasPermission(KotreenSetting.arrangementPermission)
                .then(literal("create")
                    .hasPermission(KotreenSetting.arrangementCreatePermission)
                    .executes(ArrangeCommand::createArrangement)
                )
                .then(literal("remove")
                    .hasPermission(KotreenSetting.arrangementRemovePermission)
                    .executes(ArrangeCommand::removeArrangement)
                )
                .then(literal("spawn")
                    .hasPermission(KotreenSetting.arrangementSpawnPermission)
                    .executes(ArrangeCommand::spawnArrangement)
                )
                .then(literal("kill")
                    .hasPermission(KotreenSetting.arrangementKillPermission)
                    .executes(ArrangeCommand::killArrangement)
                )
                .then(literal("action")
                    .hasPermission(KotreenSetting.arrangementActionPermission)
                    .executes(ArrangeCommand::actionArrangement)
                )
                .then(literal("stop")
                    .hasPermission(KotreenSetting.arrangementStopPermission)
                    .executes(ArrangeCommand::stopArrangement)
                )
                .then(literal("modify")
                    .hasPermission(KotreenSetting.arrangementModifyPermission)
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

    private fun createArrangement(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val desc = "."
        val pos = player.pos.toList()
        val rot = player.rotationClient.toList()
        val flying = !player.isOnGround
        val gameMode = player.interactionManager.gameMode.name
        val dimension = player.world.registryKey.value.toString()
        val arrangement = Arrangement(name, desc, pos, rot, gameMode, flying, dimension, ArrayList())

        return if (ArrangementCache.createArrangement(arrangement, context.source)) 1 else 0
    }

    private fun removeArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")

        return if (ArrangementCache.removeArrangement(name, context.source)) 1 else 0
    }

    private fun spawnArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")

        return ArrangementCache.getArrangement(name, context.source)?.spawn(context.source) ?: 0
    }

    private fun killArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")

        return ArrangementCache.getArrangement(name, context.source)?.kill(context.source) ?: 0
    }

    private fun actionArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")

        return ArrangementCache.getArrangement(name, context.source)?.action(context.source) ?: 0
    }

    private fun stopArrangement(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")

        return ArrangementCache.getArrangement(name, context.source)?.stop(context.source) ?: 0
    }

    private fun modifyDescription(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val description = StringArgumentType.getString(context, "description")

        arrangement.desc = description
        return 1
    }

    private fun modifyActionAppend(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val action = StringArgumentType.getString(context, "action")

        arrangement.actions.add(action)
        return 1
    }

    private fun modifyActionPrepare(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val action = StringArgumentType.getString(context, "action")

        arrangement.actions.add(0, action)
        return 1
    }

    private fun modifyActionAfter(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val former = IntegerArgumentType.getInteger(context, "former")
        val latter = StringArgumentType.getString(context, "latter")

        arrangement.actions.add(former + 1, latter)
        return 1
    }

    private fun modifyActionRemove(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val index = IntegerArgumentType.getInteger(context, "index")

        arrangement.actions.removeAt(index)
        return 1
    }

    private fun modifyActionOverwrite(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val index = IntegerArgumentType.getInteger(context, "index")
        val action = StringArgumentType.getString(context, "action")

        arrangement.actions.removeAt(index)
        arrangement.actions.add(action)
        return 1
    }

    private fun modifyActionsClear(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0

        arrangement.actions.removeAll(arrangement.actions.toSet())
        return 1
    }

    private fun modifyPositionHere(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0

        arrangement.pos = player.pos.toList()
        return 1
    }

    private fun modifyPositionAt(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val pos = Vec3ArgumentType.getVec3(context, "position")

        arrangement.pos = pos.toList()
        return 1
    }

    private fun modifyRotationHere(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0

        arrangement.rot = player.rotationClient.toList()
        return 1
    }

    private fun modifyRotationAt(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name, context.source) ?: return 0
        val rot = Vec2ArgumentType.getVec2(context, "rotation")

        arrangement.rot = rot.toList()
        return 1
    }
}