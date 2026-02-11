package top.htext.kotreen.config

import carpet.fakes.ServerPlayerInterface
import carpet.patches.EntityPlayerMPFake
import com.google.gson.annotations.SerializedName
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode

data class Arrangement(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("desc") var desc: String,
    @field:SerializedName("pos") var pos: List<Double>,
    @field:SerializedName("rot") var rot: List<Float>,
    @field:SerializedName("gamemode") val gameMode: String,
    @field:SerializedName("flying") var flying: Boolean,
    @field:SerializedName("dimension") val dimension: String,
    @field:SerializedName("actions") val actions: ArrayList<String>
) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Arrangement && other.hashCode() == this.hashCode()
    }

    fun spawn(source: ServerCommandSource): Int {
        val server = source.server
        if (server.playerManager.getPlayer(name) != null) return 0
        EntityPlayerMPFake.createFake(
            name,
            server,
            Vec3d(pos[0], pos[1], pos[2]),
            rot[1].toDouble(),
            rot[0].toDouble(),
            RegistryKey.of(RegistryKeys.WORLD, Identifier(dimension)),
            GameMode.valueOf(gameMode),
            flying
            //#if mcVersion == 12002
            //$$ ,{}
            //#endif
        )
        action(source)
        return 1
    }

    fun kill(source: ServerCommandSource): Int {
        val server = source.server
        val player = server.playerManager.getPlayer(name) ?: return 0
        (player as EntityPlayerMPFake).kill()
        return 1
    }

    fun action(source: ServerCommandSource): Int {
        val server = source.server
        if (server.playerManager.getPlayer(name) == null) return 0
        val commandSource = server.commandSource.withLevel(4).withSilent()
        val commandManager = server.commandManager
        actions.forEach {
            val command = "/player $name $it"
            commandManager.executeWithPrefix(commandSource, command)
        }
        return 1
    }

    fun stop(source: ServerCommandSource): Int {
        val server = source.server
        (server.playerManager.getPlayer(name) as EntityPlayerMPFake as ServerPlayerInterface).actionPack.stopAll() ?: return 0
        return 1
    }
}
