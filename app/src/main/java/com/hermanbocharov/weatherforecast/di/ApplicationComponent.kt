package com.hermanbocharov.weatherforecast.di

import android.app.Application
import android.content.Context
import com.hermanbocharov.weatherforecast.presentation.CurrentWeatherFragment
import com.hermanbocharov.weatherforecast.presentation.LocationFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(fragment: CurrentWeatherFragment)
    fun inject(fragment: LocationFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}