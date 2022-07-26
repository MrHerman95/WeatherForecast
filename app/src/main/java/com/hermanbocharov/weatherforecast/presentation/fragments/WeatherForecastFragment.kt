package com.hermanbocharov.weatherforecast.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.databinding.FragmentWeatherForecastBinding
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.recyclerview.HourlyForecastAdapter
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ForecastViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ForecastViewModel.Companion.DEFAULT_SELECTED_ITEM_POS
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class WeatherForecastFragment : Fragment() {

    private var _binding: FragmentWeatherForecastBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentWeatherForecastBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ForecastViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    private val hourlyForecastAdapter by lazy {
        HourlyForecastAdapter()
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
    }

    private fun observeViewModel() {
        viewModel.hourlyForecast.observe(viewLifecycleOwner) {
            hourlyForecastAdapter.submitList(it)
            binding.tvCityForecast.text = it[DEFAULT_SELECTED_ITEM_POS].cityName
            updateTvWeatherParameters(it[DEFAULT_SELECTED_ITEM_POS])
        }
    }

    private fun setupRecyclerView() {
        binding.rvHourlyForecast.adapter = hourlyForecastAdapter

        hourlyForecastAdapter.onHourForecastClickListener = {
            it.isSelected = true
            updateTvWeatherParameters(it)
        }
    }

    private fun updateTvWeatherParameters(item: HourlyForecast) {
        with(item) {
            binding.tvWeatherCondMain.text = description
            binding.tvCloudinessValue.text = cloudiness.toString()
            binding.tvHumidityValue.text = humidity.toString()
            binding.tvPressureValue.text = pressure.toString()
            binding.tvWindSpeedValue.text = windSpeed.toString()
            binding.tvWindGustValue.text = windGust.toString()
            binding.tvWindDirValue.text = windDegree.toString()
            binding.tvUviValue.text = uvi.toString()

            var precipitation = 0.0
            if (rain != null) {
                precipitation = rain
            } else if (snow != null) {
                precipitation = snow
            }
            binding.tvPrecipitationValue.text = precipitation.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() = WeatherForecastFragment()
    }
}