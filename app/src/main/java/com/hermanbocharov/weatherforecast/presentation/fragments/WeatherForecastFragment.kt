package com.hermanbocharov.weatherforecast.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentWeatherForecastBinding
import com.hermanbocharov.weatherforecast.domain.entities.*
import com.hermanbocharov.weatherforecast.domain.entities.Direction.EAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.WEST
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INSTANCES", "onCreate() WeatherForecastFragment")
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
            var precipitationIconId = R.drawable.ic_weather_snowy
            if (rain != null) {
                precipitation += rain
                precipitationIconId = R.drawable.ic_weather_raining
            }
            if (snow != null) {
                precipitation += snow
                precipitationIconId = R.drawable.ic_weather_snowy
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

            updateIvWindDirection(windDirection)
            updateIvHumidity(humidity)
        }
    }

    private fun updateIvWindDirection(direction: String) {
        val drawableId = when (direction) {
            NORTH -> R.drawable.ic_north
            SOUTH -> R.drawable.ic_south
            EAST -> R.drawable.ic_east
            WEST -> R.drawable.ic_west
            NORTHWEST -> R.drawable.ic_north_west
            NORTHEAST -> R.drawable.ic_north_east
            SOUTHWEST -> R.drawable.ic_south_west
            SOUTHEAST -> R.drawable.ic_south_east
            else -> throw RuntimeException("Unknown direction $direction")
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

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
        _binding = null
    }

    companion object {

        fun newInstance() = WeatherForecastFragment()
    }
}