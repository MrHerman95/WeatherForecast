package com.hermanbocharov.weatherforecast.presentation

import android.app.Application
import com.hermanbocharov.weatherforecast.di.DaggerApplicationComponent

class WeatherForecastApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}