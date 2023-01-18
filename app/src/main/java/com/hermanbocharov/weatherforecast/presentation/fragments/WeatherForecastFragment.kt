package com.hermanbocharov.weatherforecast.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentWeatherForecastBinding
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.EAST
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.NORTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.NORTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.SOUTH
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.SOUTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.SOUTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.DirectionDeg.WEST
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.entities.PrecipitationUnit
import com.hermanbocharov.weatherforecast.domain.entities.PressureUnit
import com.hermanbocharov.weatherforecast.domain.entities.SpeedUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.recyclerview.DailyForecastAdapter
import com.hermanbocharov.weatherforecast.presentation.recyclerview.HourlyForecastAdapter
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ForecastViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ForecastViewModel.Companion.DEFAULT_SELECTED_ITEM_POS
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherForecastFragment : Fragment() {

    private var _binding: FragmentWeatherForecastBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentWeatherForecastBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var disposable: Disposable
    private var snackbar: Snackbar? = null

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ForecastViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    private val hourlyForecastAdapter by lazy {
        HourlyForecastAdapter()
    }

    private val dailyForecastAdapter by lazy {
        DailyForecastAdapter()
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        postDelayTvCityAnimation()
    }

    private fun observeViewModel() {
        viewModel.hourlyForecast.observe(viewLifecycleOwner) {
            hourlyForecastAdapter.submitList(it)
            binding.tvCityForecast.text = it[DEFAULT_SELECTED_ITEM_POS].cityName
            updateTvWeatherParameters(it[DEFAULT_SELECTED_ITEM_POS])
        }

        viewModel.dailyForecast.observe(viewLifecycleOwner) {
            dailyForecastAdapter.submitList(it)
            binding.rvDailyForecast.visibility = View.VISIBLE
        }

        viewModel.hasForecastToDisplay.observe(viewLifecycleOwner) {
            if (it == false) {
                for (view in binding.fragmentWeatherForecast) {
                    view.visibility = View.INVISIBLE
                }
                snackbar = goToLocationSnackbar()
                snackbar?.show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvHourlyForecast.adapter = hourlyForecastAdapter
        binding.rvDailyForecast.adapter = dailyForecastAdapter

        hourlyForecastAdapter.onHourForecastClickListener = {
            it.isSelected = true
            updateTvWeatherParameters(it)
        }
    }

    private fun updateTvWeatherParameters(item: HourlyForecast) {
        with(item) {
            var windGustValue = windGust ?: windSpeed
            if (windGustValue < windSpeed) {
                windGustValue = windSpeed
            }

            val pressureStr: String = when (pressureUnit) {
                PressureUnit.MILLIMETERS_HG -> requireContext().getString(
                    R.string.str_pressure_value_mm,
                    pressure
                )
                PressureUnit.INCHES_HG -> requireContext().getString(
                    R.string.str_pressure_value_in,
                    pressure
                )
                else -> throw RuntimeException("Unknown pressure unit $pressureUnit")
            }

            val windDirection = convertWindDegreeToDirection(windDirectionDeg)
            val windSpeedStr: String
            val windGustStr: String
            when (windSpeedUnit) {
                SpeedUnit.METERS_PER_SECOND -> {
                    windSpeedStr = requireContext().getString(
                        R.string.str_wind_speed_value_ms, windSpeed
                    )
                    windGustStr = requireContext().getString(
                        R.string.str_wind_speed_value_ms, windGustValue
                    )
                }
                SpeedUnit.KILOMETERS_PER_HOUR -> {
                    windSpeedStr = requireContext().getString(
                        R.string.str_wind_speed_value_kph, windSpeed
                    )
                    windGustStr = requireContext().getString(
                        R.string.str_wind_speed_value_kph, windGustValue
                    )
                }
                SpeedUnit.MILES_PER_HOUR -> {
                    windSpeedStr = requireContext().getString(
                        R.string.str_wind_speed_value_mph, windSpeed
                    )
                    windGustStr = requireContext().getString(
                        R.string.str_wind_speed_value_mph, windGustValue
                    )
                }
                else -> throw RuntimeException("Unknown speed unit $windSpeedUnit")
            }

            binding.tvWeatherCondMain.text = description
            binding.tvCloudinessValue.text =
                requireContext().getString(R.string.str_cloudiness_value, cloudiness)
            binding.tvHumidityValue.text =
                requireContext().getString(R.string.str_humidity_value, humidity)
            binding.tvPressureValue.text = pressureStr
            binding.tvWindSpeedValue.text = windSpeedStr
            binding.tvWindGustValue.text = windGustStr
            binding.tvWindDirValue.text = windDirection
            binding.tvUviValue.text = String.format("%.1f", uvi)

            var precipitation = 0.0
            var precipitationIconId = R.drawable.ic_precipitation_none
            if (rain != null) {
                precipitation += rain
                precipitationIconId = R.drawable.ic_weather_raining
            }
            if (snow != null) {
                precipitation += snow
                precipitationIconId = R.drawable.ic_weather_snowing
            }
            val precipitationStr: String = when (item.precipitationUnit) {
                PrecipitationUnit.MILLIMETERS -> requireContext().getString(
                    R.string.str_precipitation_value_mm,
                    precipitation
                )
                PrecipitationUnit.INCHES -> requireContext().getString(
                    R.string.str_precipitation_value_in,
                    precipitation
                )
                else -> throw RuntimeException("Unknown precipitation unit $precipitationUnit")
            }
            binding.tvPrecipitationValue.text = precipitationStr
            binding.ivPrecipitation.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    precipitationIconId
                )
            )

            updateIvWindDirection(windDirectionDeg)
            updateIvHumidity(humidity)
        }
    }

    private fun convertWindDegreeToDirection(degree: Int): String {
        return when (degree) {
            in NORTHEAST -> getString(R.string.str_northeast)
            in EAST -> getString(R.string.str_east)
            in SOUTHEAST -> getString(R.string.str_southeast)
            in SOUTH -> getString(R.string.str_south)
            in SOUTHWEST -> getString(R.string.str_southwest)
            in WEST -> getString(R.string.str_west)
            in NORTHWEST -> getString(R.string.str_northwest)
            else -> getString(R.string.str_north)
        }
    }

    private fun updateIvWindDirection(degree: Int) {
        val drawableId = when (degree) {
            in SOUTH -> R.drawable.ic_south
            in EAST -> R.drawable.ic_east
            in WEST -> R.drawable.ic_west
            in NORTHWEST -> R.drawable.ic_north_west
            in NORTHEAST -> R.drawable.ic_north_east
            in SOUTHWEST -> R.drawable.ic_south_west
            in SOUTHEAST -> R.drawable.ic_south_east
            else -> R.drawable.ic_north
        }

        binding.ivWindDir.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                drawableId
            )
        )
    }

    private fun updateIvHumidity(humidity: Int) {
        val drawableId = when (humidity) {
            in 0..29 -> R.drawable.ic_humidity_low
            in 30..59 -> R.drawable.ic_humidity_mid
            in 60..100 -> R.drawable.ic_humidity_high
            else -> throw RuntimeException("Invalid humidity value $humidity")
        }

        binding.ivHumidity.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                drawableId
            )
        )
    }

    private fun postDelayTvCityAnimation() {
        disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.tvCityForecast.isSelected = true
            }
    }

    private fun goToLocationSnackbar(): Snackbar {
        return Snackbar.make(
            binding.fragmentWeatherForecast,
            getString(R.string.str_loc_cant_find),
            Snackbar.LENGTH_LONG
        )
            .setTextMaxLines(4)
            .setAction(getString(R.string.str_location)) {
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

        fun newInstance() = WeatherForecastFragment()
    }
}