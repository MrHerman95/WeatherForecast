package com.hermanbocharov.weatherforecast.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.data.network.ApiFactory
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weather =
            ApiFactory.apiService.getWeatherForecast(latitude = 46.482952, longitude = 30.712481)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    Log.d("TEST_OF_LOADING_DATA", "Weather forecast data loaded successfully")
                }

        val location =
            ApiFactory.apiService.getLocation(latitude = 46.482952, longitude = 30.712481)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it[0] }
                .doOnSuccess {
                    Log.d("TEST_OF_LOADING_DATA", "Location data loaded successfully")
                }

        val disposable = Single.zip(location, weather) { location, weather ->
            FullWeatherInfoDto(location, weather)
        }
            .subscribe({
                Log.d("TEST_OF_LOADING_DATA", it.toString())
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