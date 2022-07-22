package com.hermanbocharov.weatherforecast.di

import android.app.Application
import com.hermanbocharov.weatherforecast.presentation.fragments.CurrentWeatherFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.LocationFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.SettingsFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.WeatherForecastFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(fragment: CurrentWeatherFragment)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: WeatherForecastFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}