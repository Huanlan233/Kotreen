package top.htext.kotreen.serialization.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.minecraft.util.math.Vec2f

class Vec2fSerializer: JsonSerializer<Vec2f>() {
    override fun serialize(vec: Vec2f, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeNumberField("x", vec.x)
        generator.writeNumberField("y", vec.y)
        generator.writeEndObject()
    }
}