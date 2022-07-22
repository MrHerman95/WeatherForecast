package com.hermanbocharov.weatherforecast.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.usecases.GetHourlyForecastUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ForecastViewModel @Inject constructor(
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _hourlyForecast = MutableLiveData<List<HourlyForecast>>()
    val hourlyForecast: LiveData<List<HourlyForecast>>
        get() = _hourlyForecast

    init {
        getHourlyForecast()
    }

    private fun getHourlyForecast() {
        val disposable = getHourlyForecastUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _hourlyForecast.value = it
            }, {
                Log.d("TEST_OF_LOADING_DATA", "getHourlyForecast() ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}