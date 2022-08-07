package com.hermanbocharov.weatherforecast.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentLocationBinding
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.recyclerview.RecentLocationAdapter
import com.hermanbocharov.weatherforecast.presentation.recyclerview.SearchLocationAdapter
import com.hermanbocharov.weatherforecast.presentation.viewmodel.LocationViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
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

    private val searchLocationAdapter by lazy {
        SearchLocationAdapter()
    }

    private val recentLocationAdapter by lazy {
        RecentLocationAdapter()
    }

    private val compositeDisposable = CompositeDisposable()
    private var isSearchMode = false
    private var prevQuery = ""

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
        setupRecentRecyclerView()
        setupSearchModeView()
        observeViewModel()

        binding.fabDetect.setOnClickListener {
            viewModel.detectLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
        _binding = null
    }

    private fun setupSearchRecyclerView() {
        binding.rvSearchLocation.adapter = searchLocationAdapter
        binding.rvSearchLocation.itemAnimator = null

        searchLocationAdapter.onSearchLocationClickListener = {
            binding.tvLocationName.isSelected = false
            viewModel.addNewLocation(it)
            searchModeOff()
        }

        searchLocationAdapter.onSearchLocationLongClickListener = {
            val locationName = when (it.state) {
                null -> requireContext().getString(
                    R.string.str_location_city_country,
                    it.name,
                    it.country
                )
                else -> requireContext().getString(
                    R.string.str_location_full,
                    it.name,
                    it.state,
                    it.country
                )
            }
            val toast = Toast.makeText(requireContext(), locationName, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 32)
            toast.show()
        }
    }

    private fun setupRecentRecyclerView() {
        binding.rvRecentLocations.adapter = recentLocationAdapter
        recentLocationAdapter.submitList(
            listOf(
                Location("Odesa", 1.0, 1.0, "Ukraine", null),
                Location("Horqin Right Front Banner", 2.0, 1.0, "China", null),
                Location("Odesa", 1.0, 1.0, "Ukraine", null),
                Location("Gasselternijveenschemond", 2.0, 1.0, "Netherlands", null),
                Location("Odesa", 1.0, 1.0, "Ukraine", null),
                Location("Odesa", 1.0, 1.0, "Ukraine", null),
                Location("Los Angeles", 3.0, 1.0, "United States", "California")
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchModeView() {
        setupSearchRecyclerView()
        addSearchEditTextListeners()

        binding.searchViewFlow.setOnClickListener {
            if (!isSearchMode) {
                binding.tvLocationInfo.text =
                    requireContext().getString(R.string.str_search_hint_tv)
                searchModeOn()
            }
        }

        binding.ivClearEt.setOnClickListener {
            binding.etLocName.text.clear()
        }

        binding.ivCancelSearch.setOnTouchListener { view, motionEvent ->
            val iv: ImageView = view as ImageView

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateImageViewPressed(iv)
                }
                MotionEvent.ACTION_UP -> {
                    searchModeOff()
                    animateImageViewUnpressed(iv)
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateImageViewUnpressed(iv)
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun addSearchEditTextListeners() {
        binding.etLocName.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    hideClearEditTextIcon()
                } else {
                    showClearEditTextIcon()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                processInput(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        binding.etLocName.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                compositeDisposable.clear()
                val (city, country) = getPairCityCountry(textView.text.toString())
                updateListOfCities(city, country)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun processInput(input: String) {
        compositeDisposable.clear()
        val disposable = Observable.timer(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val (city, country) = getPairCityCountry(input)
                updateListOfCities(city, country)
            }
        compositeDisposable.add(disposable)
    }

    private fun getPairCityCountry(cityCountry: String): Pair<String, String> {
        val city = cityCountry.substringBefore(',').trim()
        val country = cityCountry.substringAfterLast(',', "").trim()
        return city to country
    }

    private fun updateListOfCities(city: String, country: String) {
        if (binding.etLocName.text.isNotBlank() && "$city,$country" != prevQuery) {
            prevQuery = "$city,$country"
            binding.ivLocationSearch.visibility = View.GONE
            binding.tvLocationInfo.visibility = View.GONE
            binding.rvSearchLocation.visibility = View.GONE
            binding.pbLocationSearch.visibility = View.VISIBLE
            viewModel.getListOfCities(city, country)
        }
    }

    private fun showClearEditTextIcon() {
        if (binding.ivClearEt.visibility == View.INVISIBLE) {
            binding.ivClearEt.visibility = View.VISIBLE
        }
    }

    private fun hideClearEditTextIcon() {
        binding.ivClearEt.visibility = View.INVISIBLE
    }

    private fun animateImageViewPressed(iv: ImageView) {
        iv.drawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                0xFFFFFFFF.toInt(),
                BlendModeCompat.SRC_ATOP
            )
        iv.invalidate()
        binding.cvCancelSearch.cardElevation = 2f
        binding.cvCancelSearch.useCompatPadding = true
    }

    private fun animateImageViewUnpressed(iv: ImageView) {
        iv.drawable.clearColorFilter()
        iv.invalidate()
        binding.cvCancelSearch.cardElevation = 0f
        binding.cvCancelSearch.useCompatPadding = false
    }

    private fun observeViewModel() {
        viewModel.listOfCities.observe(viewLifecycleOwner) {
            if (!isSearchMode) {
                return@observe
            }

            searchLocationAdapter.submitList(it)
            if (it.isNotEmpty()) {
                binding.ivLocationSearch.visibility = View.GONE
                binding.tvLocationInfo.visibility = View.GONE
                binding.rvSearchLocation.visibility = View.VISIBLE
            } else {
                binding.rvSearchLocation.visibility = View.INVISIBLE
                binding.ivLocationSearch.visibility = View.VISIBLE
                binding.tvLocationInfo.text = requireContext().getString(R.string.str_nothing_found)
                binding.tvLocationInfo.visibility = View.VISIBLE
            }
            binding.pbLocationSearch.visibility = View.GONE
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
        searchLocationAdapter.submitList(null)
        binding.rvSearchLocation.visibility = View.GONE

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
        binding.pbLocationSearch.visibility = View.GONE
        binding.etLocName.visibility = View.INVISIBLE
        binding.ivClearEt.visibility = View.INVISIBLE
        binding.searchViewFlow.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.search_view_bg)
        binding.etLocName.text.clear()
        prevQuery = ""
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
        binding.pbLocationSearch.visibility = View.GONE
        binding.ivClearEt.visibility = View.INVISIBLE
        binding.ivCancelSearch.visibility = View.VISIBLE
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