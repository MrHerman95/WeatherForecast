package com.hermanbocharov.weatherforecast.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.usecases.AddNewLocationUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetCurrentLocationUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetListOfCitiesUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.LoadWeatherForecastGpsLocUseCase
import com.hermanbocharov.weatherforecast.exception.NoInternetException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val getListOfCitiesUseCase: GetListOfCitiesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val addNewLocationUseCase: AddNewLocationUseCase,
    private val loadWeatherForecastGpsLocUseCase: LoadWeatherForecastGpsLocUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _listOfCities = MutableLiveData<List<Location>>()
    val listOfCities: LiveData<List<Location>>
        get() = _listOfCities

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val _isLocationDetectSuccess = MutableLiveData<Boolean>()
    val isLocationDetectSuccess: LiveData<Boolean>
        get() = _isLocationDetectSuccess

    private val _hasInternetConnection = MutableLiveData<Boolean>()
    val hasInternetConnection: LiveData<Boolean>
        get() = _hasInternetConnection

    init {
        getCurrentLocation()
    }

    fun getListOfCities(city: String, country: String) {
        val disposable = getListOfCitiesUseCase(city, country)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _listOfCities.value = it
            }, {
                if (it is NoInternetException) {
                    _hasInternetConnection.value = false
                    Log.d("TEST_OF_LOADING_DATA", "No internet connection")
                } else {
                    _hasInternetConnection.value = false
                    Log.d(
                        "TEST_OF_LOADING_DATA",
                        "Unable to fetch list of the cities ${it.message}"
                    )
                }
            })

        compositeDisposable.add(disposable)
    }

    fun addNewLocation(location: Location) {
        val disposable = addNewLocationUseCase(location)
            .flatMap { getCurrentLocationUseCase() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "detectLocation() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    private fun detectLocation() {
        val disposable = loadWeatherForecastGpsLocUseCase()
            .flatMap { getCurrentLocationUseCase() }
            .delaySubscription(DETECT_LOCATION_DELAY, TimeUnit.MILLISECONDS)
            .retry(DETECT_LOCATION_RETRY_TIMES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
                _isLocationDetectSuccess.value = true
            }, {
                Log.d("TEST_OF_LOADING_DATA", "detectLocation() ${it.message}")
                _isLocationDetectSuccess.value = false
            })

        compositeDisposable.add(disposable)
    }

    private fun getCurrentLocation() {
        val disposable = getCurrentLocationUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getCurrentLocation() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun onLocationPermissionGranted() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionGranted()")
        detectLocation()
    }

    fun onLocationPermissionDenied() {
        Log.d("TEST_OF_LOADING_DATA", "viewModel onLocationPermissionDenied()")
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val DETECT_LOCATION_DELAY = 1000L
        private const val DETECT_LOCATION_RETRY_TIMES = 3L
    }
}