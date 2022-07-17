package com.hermanbocharov.weatherforecast.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentLocationBinding
import com.hermanbocharov.weatherforecast.domain.Location
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentLocationBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[LocationViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as WeatherForecastApp).component
    }

    private val constraintSet by lazy {
        ConstraintSet()
    }

    private val imm by lazy {
        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val compositeDisposable = CompositeDisposable()
    private var isSearchMode = false
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("INSTANCES", "onCreate() LocationFragment")
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeViewModel()

        binding.ivDetectLoc.setOnClickListener {
            viewModel.detectLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
        _binding = null
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter()
        binding.rvLocation.adapter = locationAdapter

        locationAdapter.onLocationClickListener = {
            binding.tvLocationName.isSelected = false
            viewModel.addNewLocation(it)
            searchModeOff()
        }
    }

    private fun setupSearchView() {
        binding.searchViewFlow.setOnClickListener {
            if (!isSearchMode) {
                searchModeOn()
            }
        }

        binding.ivClearEt.setOnClickListener {
            binding.etLocName.text.clear()
        }

        binding.ivCancelSearch.setOnClickListener {
            searchModeOff()
        }

        binding.etLocName.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.ivClearEt.visibility = View.INVISIBLE
                } else {
                    if (binding.ivClearEt.visibility == View.INVISIBLE) {
                        binding.ivClearEt.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                compositeDisposable.clear()
                val disposable = Observable.timer(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (binding.etLocName.text.isNotBlank()) {
                            viewModel.getListOfCities(s.toString())
                        }
                    }
                compositeDisposable.add(disposable)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    private fun observeViewModel() {
        viewModel.listOfCities.observe(viewLifecycleOwner) {
            locationAdapter.submitList(it)
            if (it.isNotEmpty() && binding.ivRecyclerDiv.visibility != View.VISIBLE) {
                binding.ivRecyclerDiv.visibility = View.VISIBLE
                binding.recyclerViewFlow.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.recycler_view_bg)
            }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            binding.tvLocationName.text = requireContext().getString(
                R.string.str_location_name,
                it.name,
                it.country
            )
            postDelayTvLocationNameAnimation()
        }
    }

    private fun searchModeOff() {
        isSearchMode = false

        constraintSet.clone(requireContext(), R.layout.fragment_location)
        val transitionMove: Transition = ChangeBounds()
        val transitionFade: Transition = Fade()
        transitionMove.interpolator = AnticipateOvershootInterpolator(1.0f)
        transitionMove.duration = 500
        transitionFade.duration = 500
        val transitionSet = TransitionSet().apply {
            addTransition(transitionMove)
            addTransition(transitionFade)
        }
        TransitionManager.beginDelayedTransition(binding.fragmentLocation, transitionSet)
        constraintSet.applyTo(binding.fragmentLocation)

        hideKeyboard()
        binding.etLocName.visibility = View.INVISIBLE
        binding.ivClearEt.visibility = View.INVISIBLE
        binding.searchViewFlow.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.search_view_bg)
        binding.etLocName.text.clear()
        locationAdapter.submitList(listOf<Location>())
    }

    private fun searchModeOn() {
        isSearchMode = true

        constraintSet.clone(requireContext(), R.layout.fragment_location_search)
        val transitionFade: Transition = Fade()
        val transitionMove: Transition = ChangeBounds()
        transitionFade.duration = 500
        transitionMove.interpolator = AnticipateOvershootInterpolator(1.0f)
        transitionMove.duration = 500
        val transitionSet = TransitionSet().apply {
            addTransition(transitionFade)
            addTransition(transitionMove)
        }
        TransitionManager.beginDelayedTransition(binding.fragmentLocation, transitionSet)
        constraintSet.applyTo(binding.fragmentLocation)

        if (binding.etLocName.requestFocus()) {
            showKeyboard()
        }
        binding.searchViewFlow.background = null
        binding.recyclerViewFlow.background = null
        binding.ivClearEt.visibility = View.INVISIBLE
        binding.ivRecyclerDiv.visibility = View.GONE
    }

    private fun showKeyboard() {
        imm.showSoftInput(binding.etLocName, 0)
    }

    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.etLocName.windowToken, 0)
    }

    private fun postDelayTvLocationNameAnimation() {
        val disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.tvLocationName.isSelected = true
            }
        compositeDisposable.add(disposable)
    }

    companion object {

        fun newInstance() = LocationFragment()
    }
}