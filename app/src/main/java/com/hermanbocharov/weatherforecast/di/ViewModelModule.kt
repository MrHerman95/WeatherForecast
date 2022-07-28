package com.hermanbocharov.weatherforecast.di

import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.CurrentWeatherViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ForecastViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.LocationViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(CurrentWeatherViewModel::class)
    @Binds
    fun bindWeatherViewModel(impl: CurrentWeatherViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    @Binds
    fun bindLocationViewModel(impl: LocationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    @Binds
    fun bindSettingsViewModel(impl: SettingsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ForecastViewModel::class)
    @Binds
    fun bindForecastViewModel(impl: ForecastViewModel): ViewModel
}