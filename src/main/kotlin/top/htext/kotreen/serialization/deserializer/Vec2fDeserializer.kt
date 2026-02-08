package top.htext.kotreen.serialization.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import net.minecraft.util.math.Vec2f

class Vec2fDeserializer: JsonDeserializer<Vec2f>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Vec2f {
        val node = parser.codec.readTree<JsonNode>(parser)
        val x = node.get("x").asDouble().toFloat()
        val y = node.get("y").asDouble().toFloat()
        return Vec2f(x,y)
    }
}