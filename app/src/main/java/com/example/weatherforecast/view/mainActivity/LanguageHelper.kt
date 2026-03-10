package com.example.weatherforecast.view.mainActivity

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageHelper {

    fun setLocale(context: Context, langCode: String): Context {
        val locale = Locale(langCode) // langCode = ar
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration) // for values-ar
        config.setLocale(locale)
        config.setLayoutDirection(locale)// flips Ui directions right to left

        return context.createConfigurationContext(config)
    }
}