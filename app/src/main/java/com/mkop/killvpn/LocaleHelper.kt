package com.mkop.killvpn

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/** Per-app language (English / Persian) without any third-party library. */
object LocaleHelper {

    const val EN = "en"
    const val FA = "fa"

    private const val PREFS = "settings"
    private const val KEY_LANG = "language"

    /** Returns [base] using the language chosen in the app, or [base] itself if none was chosen. */
    fun wrap(base: Context): Context {
        val lang = base.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, null) ?: return base
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale.forLanguageTag(lang))
        return base.createConfigurationContext(config)
    }

    fun save(context: Context, lang: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANG, lang).apply()
    }

    /** The language currently in effect: [FA] or [EN] (default). */
    fun current(context: Context): String =
        if (context.resources.configuration.locales[0].language == FA) FA else EN
}
