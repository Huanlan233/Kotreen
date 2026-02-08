package top.htext.kotreen.config

import carpet.fakes.ServerPlayerInterface
import carpet.patches.EntityPlayerMPFake
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

data class Arrangement(
    val name: String,
    var desc: String,
    var pos: Vec3d,
    var rot: Vec2f,
    var flying: Boolean,
    val dimension: Identifier,
    val actions: ArrayList<String>
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
            pos,
            rot.y.toDouble(),
            rot.x.toDouble(),
            RegistryKey.of(RegistryKeys.WORLD, dimension),
            server.defaultGameMode,
            flying
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
