package com.hermanbocharov.weatherforecast.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    init {
        getCurrentLocation()
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

    fun detectLocation() {
        val disposable = loadWeatherForecastGpsLocUseCase()
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}