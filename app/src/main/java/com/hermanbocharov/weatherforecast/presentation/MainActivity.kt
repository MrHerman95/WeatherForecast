package com.hermanbocharov.weatherforecast.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.ActivityMainBinding
import com.hermanbocharov.weatherforecast.presentation.fragments.CurrentWeatherFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.LocationFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.SettingsFragment
import com.hermanbocharov.weatherforecast.presentation.fragments.WeatherForecastFragment
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarGradiant(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.selectedItemId = R.id.weather_now_page

        if (savedInstanceState == null) {
            val startFragment = CurrentWeatherFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, startFragment)
                .commit()
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            if (item.isChecked) {
                return@setOnItemSelectedListener true
            }

            val fragment = when (item.itemId) {
                R.id.location_page -> LocationFragment.newInstance()
                R.id.weather_now_page -> CurrentWeatherFragment.newInstance()
                R.id.weather_forecast_page -> WeatherForecastFragment.newInstance()
                R.id.settings_page -> SettingsFragment.newInstance()
                else -> throw RuntimeException("There is no fragment for itemId ${item.itemId}")
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()
            return@setOnItemSelectedListener true
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper(newBase?.setAppLocale(Locale.ENGLISH)))
    }

    private fun Context.setAppLocale(locale: Locale): Context {
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background = ContextCompat.getDrawable(activity, R.drawable.app_background)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
        window.navigationBarColor =
            ContextCompat.getColor(activity, android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }

}