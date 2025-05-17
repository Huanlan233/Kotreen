package top.htext.config

import net.minecraft.server.MinecraftServer

data class Series(
    val name: String,
    var desc: String,
    val arrangements: HashSet<Arrangement>
) {
    override fun equals(other: Any?): Boolean {
        return other is Series && other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun spawn(server: MinecraftServer): Int {
        var success = 0
        arrangements.forEach {
            if (it.spawn(server)) success += 1
        }
        return success
    }

    fun kill(server: MinecraftServer): Int {
        var success = 0
        arrangements.forEach {
            if (it.kill(server)) success += 1
        }
        return success
    }

    fun action(server: MinecraftServer): Int {
        var success = 0
        arrangements.forEach {
            if (it.action(server)) success += 1
        }
        return success
    }

    fun stop(server: MinecraftServer): Int {
        var success = 0
        arrangements.forEach {
            if (it.stop(server)) success += 1
        }
        return success
    }
}
