package com.hermanbocharov.weatherforecast.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.TemperatureUnitRgBinding

class SettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ViewBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("SettingsBottomSheet ViewBinding is null")

    private var layoutId = UNKNOWN_LAYOUT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        when (layoutId) {
            R.layout.temperature_unit_rg -> {
                _binding = TemperatureUnitRgBinding.inflate(inflater, container, false)
            }
            else -> throw RuntimeException("Invalid layoutId for SettingsBottomSheet")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (layoutId) {
            R.layout.temperature_unit_rg -> {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseParams() {
        val args = requireArguments()

        if (!args.containsKey(BOTTOM_SHEET_LAYOUT)) {
            throw RuntimeException("Param bottom sheet layout is absent")
        }

        val layout = args.getInt(BOTTOM_SHEET_LAYOUT, UNKNOWN_LAYOUT_ID)
        if (layout != R.layout.temperature_unit_rg) {
            throw RuntimeException("Unknown bottom sheet layout id $layout")
        }

        layoutId = layout
    }

    companion object {
        const val TAG = "SettingsBottomSheet"
        private const val BOTTOM_SHEET_LAYOUT = "bottom_sheet_layout"
        private const val UNKNOWN_LAYOUT_ID = -1

        fun newInstanceTemperature(): SettingsBottomSheet {
            return SettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(BOTTOM_SHEET_LAYOUT, R.layout.temperature_unit_rg)
                }
            }
        }

        fun newInstancePressure(): SettingsBottomSheet {
            return SettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(BOTTOM_SHEET_LAYOUT, R.layout.temperature_unit_rg)
                }
            }
        }

        fun newInstanceWindSpeed(): SettingsBottomSheet {
            return SettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(BOTTOM_SHEET_LAYOUT, R.layout.temperature_unit_rg)
                }
            }
        }

        fun newInstancePrecipitation(): SettingsBottomSheet {
            return SettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(BOTTOM_SHEET_LAYOUT, R.layout.temperature_unit_rg)
                }
            }
        }
    }
}