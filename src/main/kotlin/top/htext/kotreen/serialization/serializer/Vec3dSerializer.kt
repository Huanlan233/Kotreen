package top.htext.kotreen.serialization.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.minecraft.util.math.Vec3d

class Vec3dSerializer: JsonSerializer<Vec3d>() {
    override fun serialize(vec: Vec3d, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeNumberField("x", vec.x)
        generator.writeNumberField("y", vec.y)
        generator.writeNumberField("z", vec.z)
        generator.writeEndObject()
    }
}