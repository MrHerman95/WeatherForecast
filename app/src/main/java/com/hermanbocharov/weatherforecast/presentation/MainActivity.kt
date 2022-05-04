package com.hermanbocharov.weatherforecast.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.data.database.AppDatabase
import com.hermanbocharov.weatherforecast.data.mapper.WeatherMapper
import com.hermanbocharov.weatherforecast.data.network.ApiFactory
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var db: AppDatabase
    private val mapper = WeatherMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        val disposable = Single.zip(
            ApiFactory.apiService.getLocation(latitude = 49.2462, longitude = -123.1162)
                .subscribeOn(Schedulers.io()),
            ApiFactory.apiService.getWeatherForecast(latitude = 49.2462, longitude = -123.1162)
                .subscribeOn(Schedulers.io())
        ) { location, weather ->
            FullWeatherInfoDto(location[0], weather)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}