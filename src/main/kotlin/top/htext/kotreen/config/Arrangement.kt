package top.htext.kotreen.config

import carpet.fakes.ServerPlayerInterface
import carpet.patches.EntityPlayerMPFake
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
data class Arrangement(
    @field:JsonProperty("name") val name: String,
    @field:JsonProperty("desc") var desc: String,
    @field:JsonProperty("pos") var pos: List<Double>,
    @field:JsonProperty("rot") var rot: List<Float>,
    @field:JsonProperty("gamemode") val gameMode: String,
    @field:JsonProperty("flying") var flying: Boolean,
    @field:JsonProperty("dimension") val dimension: String,
    @field:JsonProperty("actions") val actions: ArrayList<String>
) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Arrangement && other.hashCode() == this.hashCode()
    }

    fun spawn(server: MinecraftServer): Boolean {
        if (server.playerManager.getPlayer(name) != null) return false
        EntityPlayerMPFake.createFake(
            name,
            server,

            Vec3d(pos[0], pos[1], pos[2]),
            rot[0].toDouble(),
            rot[1].toDouble(),
            RegistryKey.of(RegistryKeys.WORLD, Identifier(dimension)),
            GameMode.valueOf(gameMode),
            flying
            //#if mcVersion >= 12002
            //$$ ,{}
            //#endif
        )
        action(server)
        return true
    }

    fun kill(server: MinecraftServer): Boolean {
        val player = server.playerManager.getPlayer(name) ?: return false
        (player as EntityPlayerMPFake).kill()
        return true
    }

    fun action(server: MinecraftServer): Boolean {
        if (server.playerManager.getPlayer(name) == null) return false
        val commandSource = server.commandSource.withLevel(4).withSilent()
        val commandManager = server.commandManager
        actions.forEach {
            val command = "/player $name $it"
            commandManager.executeWithPrefix(commandSource, command)
        }
        return true
    }

    fun stop(server: MinecraftServer): Boolean {
        (server.playerManager.getPlayer(name) as EntityPlayerMPFake as ServerPlayerInterface).actionPack.stopAll() ?: return false
        return true
    }
}
