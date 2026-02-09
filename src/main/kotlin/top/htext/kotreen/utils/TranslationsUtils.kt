package top.htext.kotreen.utils

import carpet.utils.Translations
import top.htext.kotreen.Kotreen.MOD_ID

object TranslationsUtils {
    fun getTranslation(lang: String): Map<String, String> {
        return Translations.getTranslationFromResourcePath("assets/${MOD_ID}/lang/$lang.json")
    }
}