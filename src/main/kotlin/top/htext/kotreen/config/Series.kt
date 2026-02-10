package top.htext.kotreen.config

import com.google.gson.annotations.SerializedName
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import top.htext.kotreen.config.cache.ArrangementCache

data class Series(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("desc") var desc: String,
    @field:SerializedName("arrangements") val arrangements: HashSet<String>
) {
    override fun equals(other: Any?): Boolean {
        return other is Series && other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun spawn(source: ServerCommandSource): Int {
        return arrangements.sumOf {
            ArrangementCache.getArrangement(it)?.spawn(source) ?: run {
                source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
                0
            }
        }
    }

    fun kill(source: ServerCommandSource): Int {
        return arrangements.sumOf {
            ArrangementCache.getArrangement(it)?.kill(source) ?: run {
                source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
                0
            }
        }
    }

    fun action(source: ServerCommandSource): Int {
        return arrangements.sumOf {
            ArrangementCache.getArrangement(it)?.action(source) ?: run {
                source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
                0
            }
        }
    }

    fun stop(source: ServerCommandSource): Int {
        return arrangements.sumOf {
            ArrangementCache.getArrangement(it)?.stop(source) ?: run {
                source.sendError(Text.translatable("kotreen.command.failure.arrangement.null"))
                0
            }
        }
    }
}
