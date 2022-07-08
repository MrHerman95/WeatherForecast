package com.hermanbocharov.weatherforecast.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentCurrentWeatherBinding
import com.hermanbocharov.weatherforecast.domain.TemperatureUnit
import com.hermanbocharov.weatherforecast.utils.PermissionsManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentCurrentWeatherBinding is null")

    private val permissionsManager = PermissionsManager()

    private lateinit var disposable: Disposable

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INSTANCES", "onCreate() CurrentWeatherFragment")
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            "TEST_OF_LOADING_DATA",
            "Current location id curr weather frag = ${viewModel.getCurrentLocationId()}"
        )

        if (viewModel.getCurrentLocationId() == 0) {
            requestLocationPermission()
        } else {
            viewModel.getCurrentWeather()
        }

        observeViewModel()
        postDelayTvCityAnimation()
    }

    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            with(binding) {
                tvCity.text = it.cityName
                tvWeatherCondition.text = it.description
                tvTemperature.text = if (it.tempUnit == TemperatureUnit.CELSIUS) {
                    requireContext().getString(R.string.str_temp_celsius, it.temp)
                } else {
                    requireContext().getString(R.string.str_temp_fahrenheit, it.temp)
                }
                tvFeelsLike.text = if (it.tempUnit == TemperatureUnit.CELSIUS) {
                    requireContext().getString(R.string.str_feels_like_celsius, it.feelsLike)
                } else {
                    requireContext().getString(R.string.str_feels_like_fahrenheit, it.feelsLike)
                }
                tvTimezone.text = it.timezone

                tcClock.timeZone = it.timezoneName
                tcClock.visibility = View.VISIBLE
                tcDate.timeZone = it.timezoneName
                tcDate.visibility = View.VISIBLE
            }
        }
    }

    private fun postDelayTvCityAnimation() {
        disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.tvCity.isSelected = true
            }
    }

    private fun requestLocationPermission() {
        if (permissionsManager.isLocationPermissionGranted(requireContext())) {
            viewModel.onLocationPermissionGranted()
        } else {
            permissionsManager.onRequestLocationPermissionResult(this, {
                viewModel.onLocationPermissionGranted()
            }, {
                viewModel.onLocationPermissionDenied()
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
        _binding = null
    }

    companion object {

        fun newInstance() = CurrentWeatherFragment()
    }
}