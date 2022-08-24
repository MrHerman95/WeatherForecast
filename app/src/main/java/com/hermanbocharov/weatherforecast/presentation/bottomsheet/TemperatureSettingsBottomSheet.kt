package com.hermanbocharov.weatherforecast.presentation.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.TemperatureUnitRgBinding
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class TemperatureSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: TemperatureUnitRgBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("TemperatureUnitRgBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[SettingsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TemperatureUnitRgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroup()
        observeViewModel()

        binding.closeBottomSheet.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRadioGroup() {
        checkTemperatureUnitRb(viewModel.getTemperatureUnit())
        binding.bottomSheetTemperatureUnitRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsCelsius.id -> viewModel.saveTemperatureUnit(TemperatureUnit.CELSIUS)
                binding.rbSettingsFahrenheit.id -> viewModel.saveTemperatureUnit(TemperatureUnit.FAHRENHEIT)
            }
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner) {
            checkTemperatureUnitRb(it)
            binding.bottomSheetTemperatureUnitRg.jumpDrawablesToCurrentState()
        }
    }

    private fun checkTemperatureUnitRb(unit: Int) {
        when (unit) {
            TemperatureUnit.CELSIUS -> {
                binding.bottomSheetTemperatureUnitRg.check(R.id.rb_settings_celsius)
            }
            TemperatureUnit.FAHRENHEIT -> {
                binding.bottomSheetTemperatureUnitRg.check(R.id.rb_settings_fahrenheit)
            }
        }
    }

    companion object {
        const val TAG = "TemperatureSettingsBottomSheet"

        fun newInstance() = TemperatureSettingsBottomSheet()
    }
}