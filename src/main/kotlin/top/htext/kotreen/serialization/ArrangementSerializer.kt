package top.htext.kotreen.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import top.htext.kotreen.config.Arrangement

class ArrangementSerializer: JsonSerializer<Arrangement>() {
    override fun serialize(arrangement: Arrangement, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(arrangement.name)
    }
}