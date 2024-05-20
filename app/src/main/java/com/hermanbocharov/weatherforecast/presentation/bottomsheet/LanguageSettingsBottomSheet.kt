package com.hermanbocharov.weatherforecast.presentation.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.LanguageRgBinding
import com.hermanbocharov.weatherforecast.domain.entities.Language
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.viewmodel.SettingsViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import com.hermanbocharov.weatherforecast.utils.OnLanguageChangeCallback
import com.hermanbocharov.weatherforecast.utils.setAppLocale
import javax.inject.Inject

class LanguageSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LanguageRgBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("LanguageRgBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[SettingsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    private lateinit var selectedRb: Language
    private lateinit var onLanguageChangeCallback: OnLanguageChangeCallback

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LanguageRgBinding.inflate(inflater, container, false)
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

    fun setOnLanguageChangeCallback(callback: OnLanguageChangeCallback) {
        this.onLanguageChangeCallback = callback
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRadioGroup() {
        checkLanguageRb(viewModel.getAppLanguage())
        binding.bottomSheetLanguageRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSettingsEnglish.id -> {
                    viewModel.setAppLanguage(Language.English)
                    requireActivity().setAppLocale(Language.English.value)
                }
                binding.rbSettingsRussian.id -> {
                    viewModel.setAppLanguage(Language.Russian)
                    requireActivity().setAppLocale(Language.Russian.value)
                }
                binding.rbSettingsUkrainian.id -> {
                    viewModel.setAppLanguage(Language.Ukrainian)
                    requireActivity().setAppLocale(Language.Ukrainian.value)
                }
            }
            onLanguageChangeCallback.translateResources()
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.language.observe(viewLifecycleOwner) {
            checkLanguageRb(it)
            binding.bottomSheetLanguageRg.jumpDrawablesToCurrentState()
        }
    }

    private fun checkLanguageRb(language: Language) {
        when (language) {
            Language.English -> {
                binding.bottomSheetLanguageRg.check(R.id.rb_settings_english)
            }

            Language.Russian -> {
                binding.bottomSheetLanguageRg.check(R.id.rb_settings_russian)
            }

            Language.Ukrainian -> {
                binding.bottomSheetLanguageRg.check(R.id.rb_settings_ukrainian)
            }
        }
    }

    companion object {
        const val TAG = "LanguageSettingsBottomSheet"

        fun newInstance() = LanguageSettingsBottomSheet()
    }
}