package com.hermanbocharov.weatherforecast.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.usecases.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val getListOfCitiesSearchUseCase: GetListOfCitiesSearchUseCase,
    private val getListOfRecentCitiesUseCase: GetListOfRecentCitiesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val selectLocationUseCase: SelectLocationUseCase,
    private val changeLocationPinnedStateUseCase: ChangeLocationPinnedStateUseCase,
    private val addNewLocationUseCase: AddNewLocationUseCase,
    private val loadWeatherForecastGpsLocUseCase: LoadWeatherForecastGpsLocUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _listOfCitiesSearch = MutableLiveData<List<Location>>()
    val listOfCitiesSearch: LiveData<List<Location>>
        get() = _listOfCitiesSearch

    private val _listOfRecentCities = MutableLiveData<List<Location>>()
    val listOfRecentCities: LiveData<List<Location>>
        get() = _listOfRecentCities

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    init {
        getCurrentLocation()
        getListOfRecentCities()
    }

    fun getListOfCitiesSearch(city: String, country: String) {
        val disposable = getListOfCitiesSearchUseCase(city, country)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _listOfCitiesSearch.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getListOfCitiesSearch() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun addNewLocation(location: Location) {
        val disposable = addNewLocationUseCase(location)
            .map { getListOfRecentCities() }
            .flatMap { getCurrentLocationUseCase() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "addNewLocation() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun detectLocation() {
        val disposable = loadWeatherForecastGpsLocUseCase()
            .map { getListOfRecentCities() }
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

    fun selectLocation(location: Location) {
        val disposable = selectLocationUseCase(location)
            .flatMap { getCurrentLocationUseCase() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _currentLocation.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "selectLocation() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun changePinnedState(location: Location) {
        val isPinned = !location.isPinned
        val id = when (isPinned) {
            true -> location.id
            false -> 0
        }
        val changedLocation = location.copy(id = id, isPinned = isPinned)
        val disposable = changeLocationPinnedStateUseCase(changedLocation)
            .map { getListOfRecentCities() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TEST_OF_LOADING_DATA", "changePinnedState() SUCCESS $it")
            }, {
                Log.d("TEST_OF_LOADING_DATA", "changePinnedState() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    private fun getListOfRecentCities() {
        val disposable = getListOfRecentCitiesUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _listOfRecentCities.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getListOfRecentCities() ${it.message}")
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