package com.hermanbocharov.weatherforecast.di

import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.presentation.LocationViewModel
import com.hermanbocharov.weatherforecast.presentation.WeatherViewModel
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
}