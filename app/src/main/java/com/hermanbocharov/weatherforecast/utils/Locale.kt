package com.hermanbocharov.weatherforecast.utils

import android.content.Context
import android.os.LocaleList
import java.util.Locale

fun Context.setAppLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    config.setLocales(localeList)
    resources.updateConfiguration(config, resources.displayMetrics)
}