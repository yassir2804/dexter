package com.dexter.app.presentation.session

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

/**
 * Preferencia de idioma por aplicacion (per-app language), implementacion manual
 * production-ready compatible con minSdk 26.
 *
 * Por que manual y no [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]:
 * el tema base de la app es del framework (no AppCompat) y la UI es Compose puro,
 * por lo que migrar a AppCompat obligaria a cambiar la Activity base y todo el tema.
 * Este helper aplica el [Locale] al [Context] via [Context.createConfigurationContext]
 * y persiste la eleccion en [android.content.SharedPreferences] (lectura sincrona,
 * necesaria dentro de [android.app.Activity.attachBaseContext]).
 *
 * El cambio de idioma se aplica recreando la Activity ([android.app.Activity.recreate]),
 * lo que fuerza a Android a re-resolver los recursos `values-xx/` correspondientes.
 */
object LocalePreference {

    private const val PREFS_NAME = "dexter_locale_prefs"
    private const val KEY_LANGUAGE = "app_language_tag"

    /** Espanol (idioma por defecto, resuelve a `values/`). */
    const val SPANISH = "es"
    /** Ingles (resuelve a `values-en/`). */
    const val ENGLISH = "en"
    /** Portugues (resuelve a `values-pt/`). */
    const val PORTUGUESE = "pt"

    /** Idiomas soportados, en el orden en que se muestran en el selector. */
    val SUPPORTED = listOf(SPANISH, ENGLISH, PORTUGUESE)

    /** Idioma usado cuando aun no hay eleccion guardada. */
    const val DEFAULT = SPANISH

    /** Devuelve el language tag persistido, o [DEFAULT] si no hay ninguno. */
    fun getLanguageTag(context: Context): String {
        val stored = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, DEFAULT)
        return stored?.takeIf { it in SUPPORTED } ?: DEFAULT
    }

    /** Persiste el idioma elegido. Lanza si [tag] no esta en [SUPPORTED]. */
    fun setLanguageTag(context: Context, tag: String) {
        require(tag in SUPPORTED) { "Unsupported language tag: $tag" }
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_LANGUAGE, tag) }
    }

    /**
     * Envuelve [context] con el [Locale] persistido. Debe llamarse desde
     * [android.app.Activity.attachBaseContext] para que toda la jerarquia de
     * recursos use el idioma correcto desde el arranque.
     */
    fun wrap(context: Context): Context {
        val locale = Locale.forLanguageTag(getLanguageTag(context))
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
