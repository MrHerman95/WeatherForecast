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
import com.hermanbocharov.weatherforecast.databinding.FragmentSettingsBinding
import com.hermanbocharov.weatherforecast.domain.TemperatureUnit
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INSTANCES", "onCreate() SettingsFragment")
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroup()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRadioGroup() {
        binding.rgSettingsTemp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsCelsius.id -> viewModel.saveTemperatureUnit(TemperatureUnit.CELSIUS)
                binding.rbSettingsFahrenheit.id -> viewModel.saveTemperatureUnit(TemperatureUnit.FAHRENHEIT)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner) {
            when (it) {
                TemperatureUnit.CELSIUS -> binding.rgSettingsTemp.check(R.id.rb_settings_celsius)
                TemperatureUnit.FAHRENHEIT -> binding.rgSettingsTemp.check(R.id.rb_settings_fahrenheit)
            }
            binding.rgSettingsTemp.jumpDrawablesToCurrentState()
        }
    }

    companion object {

        fun newInstance() = SettingsFragment()
    }
}