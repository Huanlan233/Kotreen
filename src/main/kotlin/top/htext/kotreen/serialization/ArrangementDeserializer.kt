package top.htext.kotreen.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.cache.ArrangementCache

class ArrangementDeserializer: JsonDeserializer<Arrangement>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Arrangement? {
        val name = parser.codec.readTree<JsonNode>(parser).asText()
        return ArrangementCache.getArrangement(name)
    }
}