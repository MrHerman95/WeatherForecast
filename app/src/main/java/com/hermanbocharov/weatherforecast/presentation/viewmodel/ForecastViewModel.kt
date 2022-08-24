package com.hermanbocharov.weatherforecast.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.DailyForecast
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.usecases.GetCurrentLocationIdUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetDailyForecastUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.GetHourlyForecastUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ForecastViewModel @Inject constructor(
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val getCurrentLocationIdUseCase: GetCurrentLocationIdUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _hourlyForecast = MutableLiveData<List<HourlyForecast>>()
    val hourlyForecast: LiveData<List<HourlyForecast>>
        get() = _hourlyForecast

    private val _dailyForecast = MutableLiveData<List<DailyForecast>>()
    val dailyForecast: LiveData<List<DailyForecast>>
        get() = _dailyForecast

    private val _hasForecastToDisplay = MutableLiveData<Boolean>()
    val hasForecastToDisplay: LiveData<Boolean>
        get() = _hasForecastToDisplay

    init {
        if (getCurrentLocationIdUseCase() != 0) {
            _hasForecastToDisplay.value = true
            getHourlyForecast()
            getDailyForecast()
        } else {
            _hasForecastToDisplay.value = false
        }
    }

    private fun getHourlyForecast() {
        val disposable = getHourlyForecastUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it[DEFAULT_SELECTED_ITEM_POS].isSelected = true
                _hourlyForecast.value = it
            }, {
            })

        compositeDisposable.add(disposable)
    }

    private fun getDailyForecast() {
        val disposable = getDailyForecastUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _dailyForecast.value = it
            }, {
            })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        const val DEFAULT_SELECTED_ITEM_POS = 0
    }
}