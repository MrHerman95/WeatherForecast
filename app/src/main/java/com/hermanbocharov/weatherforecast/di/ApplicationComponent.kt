package com.hermanbocharov.weatherforecast.di

import android.app.Application
import com.hermanbocharov.weatherforecast.presentation.MainActivity
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.LanguageSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.PrecipitationSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.PressureSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.TemperatureSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.WindSpeedSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.fragments.CurrentWeatherFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.LocationFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.SettingsFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.WeatherForecastFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: CurrentWeatherFragment)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: WeatherForecastFragment)
    fun inject(bottomSheet: TemperatureSettingsBottomSheet)
    fun inject(bottomSheet: PressureSettingsBottomSheet)
    fun inject(bottomSheet: WindSpeedSettingsBottomSheet)
    fun inject(bottomSheet: PrecipitationSettingsBottomSheet)
    fun inject(bottomSheet: LanguageSettingsBottomSheet)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}