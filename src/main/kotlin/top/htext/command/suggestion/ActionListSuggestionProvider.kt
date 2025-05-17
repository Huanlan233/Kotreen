package top.htext.command.suggestion

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.config.cache.ArrangementCache
import java.util.concurrent.CompletableFuture

class ActionListSuggestionProvider: SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val name = StringArgumentType.getString(context, "arrange")
        val arrangement = ArrangementCache.getArrangement(name) ?: return builder.buildFuture()
        arrangement.actions.forEachIndexed { index, action ->
            builder.suggest(
                "$index",
                Text.translatable("kotreen.command.tooltip.action", action)
            )
        }
        return builder.buildFuture()
    }
}