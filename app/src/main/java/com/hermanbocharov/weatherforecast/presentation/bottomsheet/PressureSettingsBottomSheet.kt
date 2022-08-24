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
import com.hermanbocharov.weatherforecast.databinding.PressureUnitRgBinding
import com.hermanbocharov.weatherforecast.domain.entities.PressureUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class PressureSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PressureUnitRgBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("PressureUnitRgBinding is null")

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
        _binding = PressureUnitRgBinding.inflate(inflater, container, false)
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
        checkPressureUnitRb(viewModel.getPressureUnit())
        binding.bottomSheetPressureUnitRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsMmhg.id -> viewModel.savePressureUnit(PressureUnit.MILLIMETERS_HG)
                binding.rbSettingsInhg.id -> viewModel.savePressureUnit(PressureUnit.INCHES_HG)
            }
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.pressureUnit.observe(viewLifecycleOwner) {
            checkPressureUnitRb(it)
            binding.bottomSheetPressureUnitRg.jumpDrawablesToCurrentState()
        }
    }

    private fun checkPressureUnitRb(unit: Int) {
        when (unit) {
            PressureUnit.MILLIMETERS_HG -> {
                binding.bottomSheetPressureUnitRg.check(R.id.rb_settings_mmhg)
            }
            PressureUnit.INCHES_HG -> {
                binding.bottomSheetPressureUnitRg.check(R.id.rb_settings_inhg)
            }
        }
    }

    companion object {
        const val TAG = "PressureSettingsBottomSheet"

        fun newInstance() = PressureSettingsBottomSheet()
    }
}