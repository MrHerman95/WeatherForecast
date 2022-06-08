package com.hermanbocharov.weatherforecast.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hermanbocharov.weatherforecast.data.geolocation.GpsCoordinates
import com.hermanbocharov.weatherforecast.data.repository.WeatherRepositoryImpl
import com.hermanbocharov.weatherforecast.domain.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepositoryImpl(application)
    private val loadWeatherForecastUseCase = LoadWeatherForecastUseCase(repository)
    private val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(repository)
    private val getListOfCitiesUseCase = GetListOfCitiesUseCase(repository)
    private val compositeDisposable = CompositeDisposable()

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private val _listOfCities = MutableLiveData<List<Location>>()
    val listOfCities: LiveData<List<Location>>
        get() = _listOfCities

    private fun getCurrentWeather() {
        val disposable = getCurrentWeatherUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentWeather.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getCurrentWeather() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun getListOfCities(city: String) {
        val disposable = getListOfCitiesUseCase(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _listOfCities.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getListOfCities() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun onLocationPermissionGranted() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionGranted()")
        val disposable = loadWeatherForecastUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getCurrentWeather()
            }, {
                Log.d("TEST_OF_LOADING_DATA", "viewModel init() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun onLocationPermissionDenied() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionDenied()")
    }

    private fun onGetLocationSuccess(gpsCoordinates: GpsCoordinates) {

    }

    private fun onGetLocationError(throwable: Throwable) {

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}