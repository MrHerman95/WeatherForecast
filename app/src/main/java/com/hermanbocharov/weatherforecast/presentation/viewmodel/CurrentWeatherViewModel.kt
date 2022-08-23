package com.hermanbocharov.weatherforecast.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.usecases.GetCurrentLocationIdUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetCurrentWeatherUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.LoadWeatherForecastGpsLocUseCase
import com.hermanbocharov.weatherforecast.exception.GeolocationDisabledException
import com.hermanbocharov.weatherforecast.exception.NoInternetException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CurrentWeatherViewModel @Inject constructor(
    private val loadWeatherForecastGpsLocUseCase: LoadWeatherForecastGpsLocUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationIdUseCase: GetCurrentLocationIdUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private val _hasInternetConnection = MutableLiveData<Boolean>()
    val hasInternetConnection: LiveData<Boolean>
        get() = _hasInternetConnection

    private val _isLocationEnabled = MutableLiveData<Boolean>()
    val isLocationEnabled: LiveData<Boolean>
        get() = _isLocationEnabled

    fun getCurrentWeather() {
        val disposable = getCurrentWeatherUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _hasInternetConnection.value = true
                _currentWeather.value = it
            }, {
                if (it is NoInternetException) {
                    _hasInternetConnection.value = false
                    Log.d("TEST_OF_LOADING_DATA", "No internet connection")
                }
                else {
                    _hasInternetConnection.value = false
                    Log.d("TEST_OF_LOADING_DATA", "Unable to fetch data ${it.message}")
                }
            })

        compositeDisposable.add(disposable)
    }

    fun getCurrentLocationId(): Int {
        return getCurrentLocationIdUseCase()
    }

    fun onLocationPermissionGranted() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionGranted()")
        val disposable = loadWeatherForecastGpsLocUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _isLocationEnabled.value = true
                onGetLocationSuccess()
            }, {
                Log.d("TEST_OF_LOADING_DATA", "viewModel init() ${it.message}")
                when (it) {
                    is NoInternetException -> {
                        _hasInternetConnection.value = false
                        Log.d("TEST_OF_LOADING_DATA", "No internet connection")
                    }
                    is GeolocationDisabledException -> {
                        _isLocationEnabled.value = false
                        Log.d("TEST_OF_LOADING_DATA", "Location is disabled")
                    }
                    else -> {
                        _hasInternetConnection.value = false
                        Log.d("TEST_OF_LOADING_DATA", "Unable to fetch data ${it.message}")
                    }
                }
            })

        compositeDisposable.add(disposable)
    }

    fun onLocationPermissionDenied() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionDenied()")
    }

    private fun onGetLocationSuccess() {
        getCurrentWeather()
    }

    private fun onGetLocationError(throwable: Throwable) {

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}