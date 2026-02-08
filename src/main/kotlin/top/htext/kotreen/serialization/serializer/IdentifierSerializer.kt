package top.htext.kotreen.serialization.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.minecraft.util.Identifier

class IdentifierSerializer: JsonSerializer<Identifier>() {
    override fun serialize(identifier: Identifier, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeStringField("namespace", identifier.namespace)
        generator.writeStringField("path", identifier.path)
        generator.writeEndObject()
    }

}