package com.hermanbocharov.weatherforecast.di

import android.app.Application
import com.hermanbocharov.weatherforecast.data.database.*
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherDao
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.LocationDao
import com.hermanbocharov.weatherforecast.data.database.dao.WeatherConditionDao
import com.hermanbocharov.weatherforecast.data.network.api.ApiFactory
import com.hermanbocharov.weatherforecast.data.network.api.ApiService
import com.hermanbocharov.weatherforecast.data.repository.OpenWeatherRepositoryImpl
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindOpenWeatherRepository(impl: OpenWeatherRepositoryImpl): OpenWeatherRepository

    companion object {
        @Provides
        @ApplicationScope
        fun provideCurrentWeatherDao(application: Application): CurrentWeatherDao {
            return AppDatabase.getInstance(application).currentDao()
        }

        @Provides
        @ApplicationScope
        fun provideWeatherConditionDao(application: Application): WeatherConditionDao {
            return AppDatabase.getInstance(application).weatherConditionDao()
        }

        @Provides
        @ApplicationScope
        fun provideLocationDao(application: Application): LocationDao {
            return AppDatabase.getInstance(application).locationDao()
        }

        @Provides
        @ApplicationScope
        fun provideCurrentWeatherFullDataDao(application: Application): CurrentWeatherFullDataDao {
            return AppDatabase.getInstance(application).currentWeatherFullDataDao()
        }

        @Provides
        @ApplicationScope
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}