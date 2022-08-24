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
import com.hermanbocharov.weatherforecast.databinding.PrecipitationUnitRgBinding
import com.hermanbocharov.weatherforecast.domain.entities.PrecipitationUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject

class PrecipitationSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PrecipitationUnitRgBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("PrecipitationUnitRgBinding is null")

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
        _binding = PrecipitationUnitRgBinding.inflate(inflater, container, false)
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
        checkPrecipitationUnitRb(viewModel.getPrecipitationUnit())
        binding.bottomSheetPrecipitationUnitRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsMmh.id -> viewModel.savePrecipitationUnit(PrecipitationUnit.MILLIMETERS)
                binding.rbSettingsInh.id -> viewModel.savePrecipitationUnit(PrecipitationUnit.INCHES)
            }
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.precipitationUnit.observe(viewLifecycleOwner) {
            checkPrecipitationUnitRb(it)
            binding.bottomSheetPrecipitationUnitRg.jumpDrawablesToCurrentState()
        }
    }

    private fun checkPrecipitationUnitRb(unit: Int) {
        when (unit) {
            PrecipitationUnit.MILLIMETERS -> {
                binding.bottomSheetPrecipitationUnitRg.check(R.id.rb_settings_mmh)
            }
            PrecipitationUnit.INCHES -> {
                binding.bottomSheetPrecipitationUnitRg.check(R.id.rb_settings_inh)
            }
        }
    }

    companion object {
        const val TAG = "PrecipitationSettingsBottomSheet"

        fun newInstance() = PrecipitationSettingsBottomSheet()
    }
}