package com.hermanbocharov.weatherforecast.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hermanbocharov.weatherforecast.data.repository.OpenWeatherRepositoryImpl
import com.hermanbocharov.weatherforecast.domain.AddNewLocationUseCase
import com.hermanbocharov.weatherforecast.domain.GetCurrentLocationUseCase
import com.hermanbocharov.weatherforecast.domain.GetListOfCitiesUseCase
import com.hermanbocharov.weatherforecast.domain.Location
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OpenWeatherRepositoryImpl(application)
    private val getListOfCitiesUseCase = GetListOfCitiesUseCase(repository)
    private val getCurrentLocationUseCase = GetCurrentLocationUseCase(repository)
    private val addNewLocationUseCase = AddNewLocationUseCase(repository)
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = location
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getCurrentLocation() ${it.message}")
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