package com.hermanbocharov.weatherforecast.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.databinding.FragmentCurrentWeatherBinding
import com.hermanbocharov.weatherforecast.utils.PermissionsManager

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentCurrentWeatherBinding is null")

    private val permissionsManager = PermissionsManager()

    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FRAGMENTS", "onCreate() CurrentWeatherFragment")
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

        viewModel.currentWeather.observe(viewLifecycleOwner) {
            Log.d("TEST_OF_LOADING_DATA", it.cityName)
            Log.d("TEST_OF_LOADING_DATA", it.temp.toString())
            Log.d("TEST_OF_LOADING_DATA", it.feelsLike.toString())
            Log.d("TEST_OF_LOADING_DATA", it.description)
            Log.d("TEST_OF_LOADING_DATA", it.updateTime.toString())

            with(binding) {
                tvCity.text = it.cityName
                tvLastUpdate.text = it.updateTime.toString()
                tvWeatherCondition.text = it.description
                tvTemperature.text = it.temp.toString()
                tvFeelsLike.text = it.feelsLike.toString()
            }
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
        _binding = null
    }

    companion object {

        fun newInstance() = CurrentWeatherFragment()
    }
}