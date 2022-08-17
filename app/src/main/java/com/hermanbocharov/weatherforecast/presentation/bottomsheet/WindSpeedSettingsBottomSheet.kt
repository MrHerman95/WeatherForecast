package com.hermanbocharov.weatherforecast.presentation.bottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.WindSpeedUnitRgBinding
import com.hermanbocharov.weatherforecast.domain.entities.SpeedUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class WindSpeedSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: WindSpeedUnitRgBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("WindSpeedUnitRgBinding is null")

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
        _binding = WindSpeedUnitRgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroup()
        Log.d("TEST_OF_LOADING_DATA", viewModel.toString())
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
        binding.bottomSheetWindSpeedUnitRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsMs.id -> viewModel.saveSpeedUnit(SpeedUnit.METERS_PER_SECOND)
                binding.rbSettingsKmh.id -> viewModel.saveSpeedUnit(SpeedUnit.KILOMETERS_PER_HOUR)
                binding.rbSettingsMph.id -> viewModel.saveSpeedUnit(SpeedUnit.MILES_PER_HOUR)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.speedUnit.observe(viewLifecycleOwner) {
            when (it) {
                SpeedUnit.METERS_PER_SECOND -> {
                    binding.bottomSheetWindSpeedUnitRg.check(R.id.rb_settings_ms)
                }
                SpeedUnit.KILOMETERS_PER_HOUR -> {
                    binding.bottomSheetWindSpeedUnitRg.check(R.id.rb_settings_kmh)
                }
                SpeedUnit.MILES_PER_HOUR -> {
                    binding.bottomSheetWindSpeedUnitRg.check(R.id.rb_settings_mph)
                }
            }
            binding.bottomSheetWindSpeedUnitRg.jumpDrawablesToCurrentState()
        }
    }

    companion object {
        const val TAG = "WindSpeedSettingsBottomSheet"

        fun newInstance() = WindSpeedSettingsBottomSheet()
    }
}