package com.hermanbocharov.weatherforecast.di

import android.app.Application
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.TemperatureSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.fragments.*
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(fragment: CurrentWeatherFragment)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: WeatherForecastFragment)
    fun inject(bottomSheet: TemperatureSettingsBottomSheet)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}