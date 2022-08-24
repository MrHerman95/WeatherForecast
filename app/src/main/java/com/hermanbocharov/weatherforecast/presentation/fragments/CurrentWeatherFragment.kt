package com.hermanbocharov.weatherforecast.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentCurrentWeatherBinding
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.CELSIUS
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit.FAHRENHEIT
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.CurrentWeatherViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
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

    private lateinit var disposable: Disposable
    private var snackbar: Snackbar? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CurrentWeatherViewModel::class.java]
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

        binding.btnLoadWeatherRetry.setOnClickListener {
            binding.btnLoadWeatherRetry.visibility = View.INVISIBLE
            binding.pbCurrentWeather.visibility = View.VISIBLE
            viewModel.getCurrentWeather()
        }
    }

    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            with(binding) {
                val tempResId: Int
                val feelsLikeResId: Int
                when (it.tempUnit) {
                    CELSIUS -> {
                        tempResId = R.string.str_temp_celsius
                        feelsLikeResId = R.string.str_feels_like_celsius
                    }
                    FAHRENHEIT -> {
                        tempResId = R.string.str_temp_fahrenheit
                        feelsLikeResId = R.string.str_feels_like_fahrenheit
                    }
                    else -> throw RuntimeException("Unknown temperature unit ${it.tempUnit}")
                }

                pbCurrentWeather.visibility = View.INVISIBLE

                tvTemperature.text = requireContext().getString(tempResId, it.temp)
                tvFeelsLike.text = requireContext().getString(feelsLikeResId, it.feelsLike)
                tvCity.text = it.cityName
                tvWeatherCondition.text = it.description
                tvTimezone.text = it.timezone
                tcClock.timeZone = it.timezoneName
                tcClock.visibility = View.VISIBLE
                tcDate.timeZone = it.timezoneName
                tcDate.visibility = View.VISIBLE

                val context = binding.ivWeatherCondition.context
                val weatherIconId =
                    context.resources.getIdentifier(it.weatherIcon, "drawable", context.packageName)
                ivWeatherCondition.setImageResource(weatherIconId)
            }
        }

        viewModel.hasInternetConnection.observe(viewLifecycleOwner) {
            if (it == false) {
                Toast.makeText(
                    requireContext(),
                    "Couldn't refresh the forecast. Please check your internet connection and try again.",
                    Toast.LENGTH_LONG
                ).show()

                binding.pbCurrentWeather.visibility = View.INVISIBLE
                binding.btnLoadWeatherRetry.visibility = View.VISIBLE
            }
        }

        viewModel.isLocationEnabled.observe(viewLifecycleOwner) {
            if (it == false) {
                snackbar = goToLocationSnackbar("Couldn't find your location. Select a\u00A0location manually in the\u00A0\"Location\" menu")
                snackbar?.show()

                binding.pbCurrentWeather.visibility = View.INVISIBLE
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
        if (PermissionsManager.isLocationPermissionGranted(requireContext())) {
            viewModel.onLocationPermissionGranted()
        } else {
            PermissionsManager.onRequestLocationPermissionResult(this, {
                viewModel.onLocationPermissionGranted()
            }, {
                viewModel.onLocationPermissionDenied()
                showOnLocationPermissionDeniedSnackbar()
            })
        }
    }

    private fun showOnLocationPermissionDeniedSnackbar() {
        binding.pbCurrentWeather.visibility = View.INVISIBLE
        snackbar = goToLocationSnackbar("Location permission denied. Select a\u00A0location manually in the\u00A0\"Location\" menu")
        snackbar?.show()
    }

    private fun goToLocationSnackbar(text: String): Snackbar {
        return Snackbar.make(
            binding.fragmentCurrentWeather,
            text,
            Snackbar.LENGTH_LONG
        )
            .setTextMaxLines(4)
            .setAction("Location") {
                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
                bottomNav.selectedItemId = R.id.location_page
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        disposable.dispose()
        _binding = null
    }

    companion object {

        fun newInstance() = CurrentWeatherFragment()
    }
}