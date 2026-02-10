package top.htext.kotreen.serialization

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import top.htext.kotreen.config.Arrangement
import top.htext.kotreen.config.cache.ArrangementCache

class ArrangementAdapter: TypeAdapter<Arrangement>() {
    override fun write(writer: JsonWriter, arrangement: Arrangement) {
        writer.value(arrangement.name)
    }

    override fun read(reader: JsonReader): Arrangement? {
        val name = reader.nextString()
        return ArrangementCache.getArrangement(name)
    }
}