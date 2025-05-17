package top.htext.command.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.config.cache.ArrangementCache
import java.util.concurrent.CompletableFuture

class ArrangementSuggestionProvider: SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        ArrangementCache.getCache().forEach {
            builder.suggest(
                it.name,
                Text.translatable("kotreen.command.tooltip.description", it.desc)
            )
        }
        return builder.buildFuture()
    }
}