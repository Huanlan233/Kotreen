package top.htext.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import net.minecraft.util.math.Vec3d

class Vec3dDeserializer: JsonDeserializer<Vec3d>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Vec3d {
        val node = parser.codec.readTree<JsonNode>(parser)
        val x = node.get("x").asDouble()
        val y = node.get("y").asDouble()
        val z = node.get("z").asDouble()
        return Vec3d(x,y,z)
    }
}