package com.hermanbocharov.weatherforecast.di

import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.LocationViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    @Binds
    fun bindWeatherViewModel(impl: WeatherViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    @Binds
    fun bindLocationViewModel(impl: LocationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    @Binds
    fun bindSettingsViewModel(impl: SettingsViewModel): ViewModel
}