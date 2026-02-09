package top.htext.kotreen.command.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import top.htext.kotreen.config.cache.ArrangementCache
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class ArrangementListSuggestionProvider: SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val currentInput = builder.input // /series xxx create a b c d
        //ugly but it works
        val currentOptions = currentInput.split(" ").drop(3).toMutableList() // [a,b,c,d]
        val currentText = currentOptions.stream().collect(Collectors.joining(" ")) // "a b c d"

        val allOptions = HashSet<String>() // [a,b,c,d,e,f,g]
        ArrangementCache.getCache().forEach { allOptions.add(it.name) }

        val remainingOptions = allOptions - currentOptions.toHashSet() // [e,f,g]
        remainingOptions.forEach { builder.suggest("$currentText $it") }
        return builder.buildFuture()
    }
}