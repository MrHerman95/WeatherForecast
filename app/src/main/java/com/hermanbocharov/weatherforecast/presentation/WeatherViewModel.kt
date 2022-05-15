package com.hermanbocharov.weatherforecast.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hermanbocharov.weatherforecast.data.preferences.PreferenceManager
import com.hermanbocharov.weatherforecast.data.repository.WeatherRepositoryImpl
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.GetCurrentWeatherUseCase
import com.hermanbocharov.weatherforecast.domain.LoadWeatherForecastUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepositoryImpl(application)
    private val loadWeatherForecastUseCase = LoadWeatherForecastUseCase(repository)
    private val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(repository)
    private val compositeDisposable = CompositeDisposable()

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

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

    init {
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}