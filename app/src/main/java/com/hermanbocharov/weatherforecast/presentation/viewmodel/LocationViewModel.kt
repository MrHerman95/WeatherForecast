package com.hermanbocharov.weatherforecast.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.usecases.GetCurrentLocationUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetListOfCitiesUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.LoadWeatherForecastGpsLocUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.SetNewLocationUseCase
import com.hermanbocharov.weatherforecast.exception.GeolocationDisabledException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val getListOfCitiesUseCase: GetListOfCitiesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val setNewLocationUseCase: SetNewLocationUseCase,
    private val loadWeatherForecastGpsLocUseCase: LoadWeatherForecastGpsLocUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _listOfCities = MutableLiveData<List<Location>>()
    val listOfCities: LiveData<List<Location>>
        get() = _listOfCities

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?>
        get() = _currentLocation

    private val _isLocationDetectSuccess = MutableLiveData<Boolean>()
    val isLocationDetectSuccess: LiveData<Boolean>
        get() = _isLocationDetectSuccess

    private val _hasInternetConnectionSearch = MutableLiveData<Boolean>()
    val hasInternetConnectionSearch: LiveData<Boolean>
        get() = _hasInternetConnectionSearch

    private val _hasInternetConnectionDetect = MutableLiveData<Boolean>()
    val hasInternetConnectionDetect: LiveData<Boolean>
        get() = _hasInternetConnectionDetect

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
                _hasInternetConnectionSearch.value = false
            })

        compositeDisposable.add(disposable)
    }

    fun setNewLocation(location: Location) {
        val disposable = setNewLocationUseCase(location)
            .flatMap { getCurrentLocationUseCase() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
            }, {
            })

        compositeDisposable.add(disposable)
    }

    private fun detectLocation() {
        val disposable = loadWeatherForecastGpsLocUseCase()
            .flatMap { getCurrentLocationUseCase() }
            .delaySubscription(DETECT_LOCATION_DELAY, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
                _isLocationDetectSuccess.value = true
            }, {
                when (it) {
                    is GeolocationDisabledException -> _isLocationDetectSuccess.value = false
                    else -> _hasInternetConnectionDetect.value = false
                }
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
                _currentLocation.value = null
            })

        compositeDisposable.add(disposable)
    }

    fun onLocationPermissionGranted() {
        detectLocation()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val DETECT_LOCATION_DELAY = 1000L
    }
}