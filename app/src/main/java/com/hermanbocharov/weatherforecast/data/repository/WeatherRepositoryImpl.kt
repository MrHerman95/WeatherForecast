package com.hermanbocharov.weatherforecast.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hermanbocharov.weatherforecast.data.database.AppDatabase
import com.hermanbocharov.weatherforecast.data.mapper.WeatherMapper
import com.hermanbocharov.weatherforecast.data.network.ApiFactory
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.WeatherRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherRepositoryImpl(
    private val application: Application
) : WeatherRepository {

    private val compositeDisposable = CompositeDisposable()
    private val apiService = ApiFactory.apiService
    private val db = AppDatabase.getInstance(application)
    private val mapper = WeatherMapper()

    override fun getCurrentWeather(): LiveData<CurrentWeather> {
        return Transformations.map(db.currentWeatherFullDataDao().getCurrentWeatherCompleteData()) {
            if (it.isNotEmpty()) mapper.mapEntityToCurrentWeatherDomain(it[0])
            else null
        }
    }

    override fun loadData() {
        val locationDto =
            //apiService.getLocation(latitude = 49.2462, longitude = -123.1162) // Vancouver, CA
            apiService.getLocation(latitude = 46.4829, longitude = 30.7125) // Odessa, UA
                .subscribeOn(Schedulers.io())

        val weatherForecastDto =
            //apiService.getWeatherForecast(latitude = 49.2462, longitude = -123.1162) // Vancouver, CA
            apiService.getWeatherForecast(latitude = 46.4829, longitude = 30.7125) // Odessa, UA
                .subscribeOn(Schedulers.io())

        val disposable = Single.zip(locationDto, weatherForecastDto) {
            location, weather -> FullWeatherInfoDto(location[0], weather)
        }
            .observeOn(Schedulers.io())
            .subscribe({
                Log.d("TEST_OF_LOADING_DATA", it.toString())

                db.weatherConditionDao().insertWeatherCondition(
                    mapper.mapWeatherConditionDtoToEntity(it.weatherForecast.current.weather[0])
                )

                val locationId = db.locationDao().insertLocation(
                    mapper.mapLocationDtoToEntity(it.location)
                )

                db.currentDao().insertCurrentWeather(
                    mapper.mapCurrentWeatherDtoToEntity(
                        it.weatherForecast.current, locationId.toInt()
                    )
                )
            }, {
                Log.d("TEST_OF_LOADING_DATA", it.message + "")
            })

        compositeDisposable.add(disposable)
    }
}