package com.hermanbocharov.weatherforecast.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentSettingsBinding
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
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
        setupOnViewContainerTouchListener(binding.tempViewContainer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOnViewContainerTouchListener(v: View) {
        v.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateViewContainerPressed(view)
                }
                MotionEvent.ACTION_UP -> {
                    animateViewContainerUnpressed(view)
                    val settingsBottomSheet = SettingsBottomSheet.newInstanceTemperature()
                    settingsBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        SettingsBottomSheet.TAG
                    )
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateViewContainerUnpressed(view)
                }
                MotionEvent.ACTION_MOVE -> {
                }
            }

            return@setOnTouchListener true
        }
    }

    private fun animateViewContainerPressed(v: View) {
        v.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                0x33FFFFFF,
                BlendModeCompat.OVERLAY
            )
        v.invalidate()
    }

    private fun animateViewContainerUnpressed(v: View) {
        v.background.clearColorFilter()
        v.invalidate()
    }

    private fun setupRadioGroup() {
        /*binding.rgSettingsTemp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsCelsius.id -> viewModel.saveTemperatureUnit(TemperatureUnit.CELSIUS)
                binding.rbSettingsFahrenheit.id -> viewModel.saveTemperatureUnit(TemperatureUnit.FAHRENHEIT)
            }
        }*/
    }

    private fun observeViewModel() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner) {
            when (it) {
                TemperatureUnit.CELSIUS -> {
                    binding.tvSettingsTempUnit.text =
                        requireContext().getString(R.string.str_settings_celsius)
                    //binding.rgSettingsTemp.check(R.id.rb_settings_celsius)
                }
                TemperatureUnit.FAHRENHEIT -> {
                    binding.tvSettingsTempUnit.text =
                        requireContext().getString(R.string.str_settings_fahrenheit)
                    //binding.rgSettingsTemp.check(R.id.rb_settings_fahrenheit)
                }
            }
            //binding.rgSettingsTemp.jumpDrawablesToCurrentState()
        }
    }

    companion object {

        fun newInstance() = SettingsFragment()
    }
}