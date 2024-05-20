package com.hermanbocharov.weatherforecast.presentation.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hermanbocharov.weatherforecast.BuildConfig
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentSettingsBinding
import com.hermanbocharov.weatherforecast.domain.entities.Language
import com.hermanbocharov.weatherforecast.domain.entities.PrecipitationUnit
import com.hermanbocharov.weatherforecast.domain.entities.PressureUnit
import com.hermanbocharov.weatherforecast.domain.entities.SpeedUnit
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.LanguageSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.PrecipitationSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.PressureSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.TemperatureSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.bottomsheet.WindSpeedSettingsBottomSheet
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import com.hermanbocharov.weatherforecast.utils.OnLanguageChangeCallback
import javax.inject.Inject


class SettingsFragment : Fragment(), OnLanguageChangeCallback {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[SettingsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    private var isMotionEventCanceled = false

    private lateinit var languageBottomSheet: LanguageSettingsBottomSheet

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
        observeViewModel()
        setupOnViewContainerTouchListener(binding.tempViewContainer)
        setupOnViewContainerTouchListener(binding.pressureViewContainer)
        setupOnViewContainerTouchListener(binding.precipitationViewContainer)
        setupOnViewContainerTouchListener(binding.windSpeedViewContainer)
        setupOnViewContainerTouchListener(binding.languageContainer)
        setupOnViewContainerTouchListener(binding.rateContainer)
        setupOnViewContainerTouchListener(binding.shareContainer)
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
                    isMotionEventCanceled = false
                }

                MotionEvent.ACTION_UP -> {
                    if (isMotionEventCanceled) {
                        return@setOnTouchListener false
                    }
                    animateViewContainerUnpressed(view)
                    synchronized(LOCK) {
                        performTouch(view)
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    animateViewContainerUnpressed(view)
                    isMotionEventCanceled = true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isMotionEventCanceled) {
                        return@setOnTouchListener false
                    }
                    if (!isTouchOnView(v, motionEvent)) {
                        motionEvent.action = MotionEvent.ACTION_CANCEL
                        v.dispatchTouchEvent(motionEvent)
                    }
                }
            }

            return@setOnTouchListener true
        }
    }

    private fun performTouch(view: View) {
        with(binding) {
            when (view) {
                tempViewContainer, pressureViewContainer, precipitationViewContainer, windSpeedViewContainer, languageContainer -> {
                    showBottomSheet(view)
                }

                rateContainer -> {
                    rateAppIntent()
                }

                shareContainer -> {
                    shareLinkIntent()
                }

                else -> throw RuntimeException("Invalid view $view to perform touch")
            }
        }
    }

    private fun showBottomSheet(view: View) {
        if (isAnyBottomSheetShown())
            return

        with(binding) {
            when (view) {
                tempViewContainer -> {
                    val bottomSheet = TemperatureSettingsBottomSheet.newInstance()
                    bottomSheet.show(childFragmentManager, TemperatureSettingsBottomSheet.TAG)
                }

                pressureViewContainer -> {
                    val bottomSheet = PressureSettingsBottomSheet.newInstance()
                    bottomSheet.show(childFragmentManager, PressureSettingsBottomSheet.TAG)
                }

                precipitationViewContainer -> {
                    val bottomSheet = PrecipitationSettingsBottomSheet.newInstance()
                    bottomSheet.show(childFragmentManager, PrecipitationSettingsBottomSheet.TAG)
                }

                windSpeedViewContainer -> {
                    val bottomSheet = WindSpeedSettingsBottomSheet.newInstance()
                    bottomSheet.show(childFragmentManager, WindSpeedSettingsBottomSheet.TAG)
                }

                languageContainer -> {
                    languageBottomSheet = LanguageSettingsBottomSheet.newInstance()
                    languageBottomSheet.show(childFragmentManager, LanguageSettingsBottomSheet.TAG)
                    languageBottomSheet.setOnLanguageChangeCallback(this@SettingsFragment)
                }

                else -> throw RuntimeException("Invalid view $view to show bottom sheet")
            }
        }
    }

    private fun isAnyBottomSheetShown(): Boolean {
        return childFragmentManager.findFragmentByTag(TemperatureSettingsBottomSheet.TAG) != null ||
                childFragmentManager.findFragmentByTag(PressureSettingsBottomSheet.TAG) != null ||
                childFragmentManager.findFragmentByTag(PrecipitationSettingsBottomSheet.TAG) != null ||
                childFragmentManager.findFragmentByTag(WindSpeedSettingsBottomSheet.TAG) != null ||
                childFragmentManager.findFragmentByTag(LanguageSettingsBottomSheet.TAG) != null
    }

    private fun shareLinkIntent() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Weather Forecast")
            var shareMessage =
                """
                Let me recommend you the following app: Weather Forecast.
                Always stay up to date with the latest changes in the weather forecast.

                Play Store - Download link for Android:
                """.trimIndent()
            shareMessage =
                "${shareMessage}\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share"))
        } catch (e: Exception) {
        }
    }

    private fun rateAppIntent() {
        val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToPlayStore = Intent(Intent.ACTION_VIEW, uri)

        // After pressing the back button in Play Store to return to our application,
        // we need to add following flags to intent.
        goToPlayStore.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToPlayStore)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    private fun isTouchOnView(v: View, ev: MotionEvent): Boolean {
        return ev.x >= -ACCURACY_ERROR_VAL &&
                ev.x <= v.width + ACCURACY_ERROR_VAL &&
                ev.y >= -ACCURACY_ERROR_VAL &&
                ev.y <= v.height + ACCURACY_ERROR_VAL
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

    private fun observeViewModel() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner) {
            when (it) {
                TemperatureUnit.CELSIUS -> {
                    binding.tvSettingsTempUnit.text =
                        requireContext().getString(R.string.str_settings_celsius)
                }

                TemperatureUnit.FAHRENHEIT -> {
                    binding.tvSettingsTempUnit.text =
                        requireContext().getString(R.string.str_settings_fahrenheit)
                }
            }
        }

        viewModel.pressureUnit.observe(viewLifecycleOwner) {
            when (it) {
                PressureUnit.MILLIMETERS_HG -> {
                    binding.tvSettingsPressureUnit.text =
                        requireContext().getString(R.string.str_settings_mmhg)
                }

                PressureUnit.INCHES_HG -> {
                    binding.tvSettingsPressureUnit.text =
                        requireContext().getString(R.string.str_settings_inhg)
                }
            }
        }

        viewModel.precipitationUnit.observe(viewLifecycleOwner) {
            when (it) {
                PrecipitationUnit.MILLIMETERS -> {
                    binding.tvSettingsPrecipitationUnit.text =
                        requireContext().getString(R.string.str_settings_mmh)
                }

                PrecipitationUnit.INCHES -> {
                    binding.tvSettingsPrecipitationUnit.text =
                        requireContext().getString(R.string.str_settings_inh)
                }
            }
        }

        viewModel.speedUnit.observe(viewLifecycleOwner) {
            when (it) {
                SpeedUnit.METERS_PER_SECOND -> {
                    binding.tvSettingsWindSpeedUnit.text =
                        requireContext().getString(R.string.str_settings_ms)
                }

                SpeedUnit.KILOMETERS_PER_HOUR -> {
                    binding.tvSettingsWindSpeedUnit.text =
                        requireContext().getString(R.string.str_settings_kmh)
                }

                SpeedUnit.MILES_PER_HOUR -> {
                    binding.tvSettingsWindSpeedUnit.text =
                        requireContext().getString(R.string.str_settings_mph)
                }
            }
        }

        viewModel.language.observe(viewLifecycleOwner) {
            when (it) {
                Language.English -> {
                    binding.tvLanguage.text =
                        requireContext().getString(R.string.english)
                }

                Language.Russian -> {
                    binding.tvLanguage.text =
                        requireContext().getString(R.string.russian)
                }

                Language.Ukrainian -> {
                    binding.tvLanguage.text =
                        requireContext().getString(R.string.ukrainian)
                }
            }
        }
    }

    override fun translateResources() {
        val bottomNavigationMenu =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        with(requireActivity().baseContext) {
            bottomNavigationMenu.menu.findItem(R.id.location_page).title =
                getString(R.string.str_location)

            bottomNavigationMenu.menu.findItem(R.id.weather_now_page).title =
                getString(R.string.str_weather_now)

            bottomNavigationMenu.menu.findItem(R.id.weather_forecast_page).title =
                getString(R.string.str_weather_forecast)

            bottomNavigationMenu.menu.findItem(R.id.settings_page).title =
                getString(R.string.str_settings)

            binding.tvUnits.text = getString(R.string.str_settings_units)
            binding.tvSettingsTemp.text = getString(R.string.str_settings_temp)
            binding.tvSettingsWindSpeed.text = getString(R.string.str_settings_wind_speed)
            binding.tvSettingsPrecipitation.text = getString(R.string.str_settings_precipitation)
            binding.tvSettingsPressure.text = getString(R.string.str_settings_pressure)
            binding.tvRateShare.text = getString(R.string.str_rate_share)
            binding.tvRate.text = getString(R.string.str_rate)
            binding.tvShare.text = getString(R.string.str_share)
            binding.tvRateDesc.text = getString(R.string.str_rate_desc)
            binding.tvShareDesc.text = getString(R.string.str_share_desc)
            binding.tvAppLanguage.text = getString(R.string.language)
            binding.tvAppVersion.text = getString(R.string.str_app_name_version)

            when (viewModel.temperatureUnit.value) {
                TemperatureUnit.CELSIUS -> {
                    binding.tvSettingsTempUnit.text = getString(R.string.str_settings_celsius)
                }

                TemperatureUnit.FAHRENHEIT -> {
                    binding.tvSettingsTempUnit.text = getString(R.string.str_settings_fahrenheit)
                }
            }

            when (viewModel.pressureUnit.value) {
                PressureUnit.MILLIMETERS_HG -> {
                    binding.tvSettingsPressureUnit.text = getString(R.string.str_settings_mmhg)
                }

                PressureUnit.INCHES_HG -> {
                    binding.tvSettingsPressureUnit.text = getString(R.string.str_settings_inhg)
                }
            }

            when (viewModel.speedUnit.value) {
                SpeedUnit.METERS_PER_SECOND -> {
                    binding.tvSettingsWindSpeedUnit.text = getString(R.string.str_settings_ms)
                }

                SpeedUnit.KILOMETERS_PER_HOUR -> {
                    binding.tvSettingsWindSpeedUnit.text = getString(R.string.str_settings_kmh)
                }

                SpeedUnit.MILES_PER_HOUR -> {
                    binding.tvSettingsWindSpeedUnit.text = getString(R.string.str_settings_mph)
                }
            }

            when (viewModel.precipitationUnit.value) {
                PrecipitationUnit.MILLIMETERS -> {
                    binding.tvSettingsPrecipitationUnit.text = getString(R.string.str_settings_mmh)
                }

                PrecipitationUnit.INCHES -> {
                    binding.tvSettingsPrecipitationUnit.text = getString(R.string.str_settings_inh)
                }
            }
        }
    }

    companion object {

        fun newInstance() = SettingsFragment()

        private val LOCK = Any()
        private const val ACCURACY_ERROR_VAL = 30
    }
}