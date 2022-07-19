package com.hermanbocharov.weatherforecast.di

import android.app.Application
import com.hermanbocharov.weatherforecast.data.database.AppDatabase
import com.hermanbocharov.weatherforecast.data.database.dao.*
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
        fun provideHourlyForecastDao(application: Application): HourlyForecastDao {
            return AppDatabase.getInstance(application).hourlyDao()
        }

        @Provides
        @ApplicationScope
        fun provideDailyForecastDao(application: Application): DailyForecastDao {
            return AppDatabase.getInstance(application).dailyDao()
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
        fun provideHourlyForecastFullDataDao(application: Application): HourlyForecastFullDataDao {
            return AppDatabase.getInstance(application).hourlyForecastFullDataDao()
        }

        @Provides
        @ApplicationScope
        fun provideDailyForecastFullDataDao(application: Application): DailyForecastFullDataDao {
            return AppDatabase.getInstance(application).dailyForecastFullDataDao()
        }

        @Provides
        @ApplicationScope
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}