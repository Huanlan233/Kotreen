package top.htext.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import net.minecraft.util.Identifier

class IdentifierDeserializer: JsonDeserializer<Identifier>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Identifier {
        val node = parser.codec.readTree<JsonNode>(parser)
        val namespace = node.get("namespace").asText()
        val path = node.get("path").asText()
        return Identifier(namespace, path)
    }
}