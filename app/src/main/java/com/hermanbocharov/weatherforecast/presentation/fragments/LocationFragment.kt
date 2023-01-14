package com.hermanbocharov.weatherforecast.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
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
import com.google.android.material.snackbar.Snackbar
import com.hermanbocharov.weatherforecast.BuildConfig
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentLocationBinding
import com.hermanbocharov.weatherforecast.presentation.WeatherForecastApp
import com.hermanbocharov.weatherforecast.presentation.recyclerview.LocationAdapter
import com.hermanbocharov.weatherforecast.presentation.viewmodel.LocationViewModel
import com.hermanbocharov.weatherforecast.presentation.viewmodel.ViewModelFactory
import com.hermanbocharov.weatherforecast.utils.PermissionsManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sqrt


class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentLocationBinding is null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var snackbar: Snackbar? = null

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

    private val locationAdapter by lazy {
        LocationAdapter()
    }

    private val compositeDisposable = CompositeDisposable()
    private var isSearchMode = false
    private var prevQuery = ""
    private var isMotionEventCanceled = false

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
        setupSearchModeView()
        observeViewModel()

        binding.fabDetect.setOnClickListener {
            requestLocationPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        compositeDisposable.dispose()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.rvLocation.adapter = locationAdapter
        binding.rvLocation.itemAnimator = null

        locationAdapter.onLocationClickListener = {
            binding.tvLocationName.isSelected = false
            viewModel.addNewLocation(it)
            searchModeOff()
        }

        locationAdapter.onLocationLongClickListener = {
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

    private fun setupSearchModeView() {
        setupRecyclerView()
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

        setupOnCancelSearchTouchListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOnCancelSearchTouchListener() {
        binding.ivCancelSearch.setOnTouchListener { view, motionEvent ->
            val iv: ImageView = view as ImageView

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateImageViewPressed(iv)
                    isMotionEventCanceled = false
                }
                MotionEvent.ACTION_UP -> {
                    if (isMotionEventCanceled) {
                        return@setOnTouchListener false
                    }
                    searchModeOff()
                    animateImageViewUnpressed(iv)
                }
                MotionEvent.ACTION_CANCEL -> {
                    animateImageViewUnpressed(iv)
                    isMotionEventCanceled = true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isMotionEventCanceled) {
                        return@setOnTouchListener false
                    }
                    val distance = getMoveDistance(iv.width / 2f, iv.height / 2f, motionEvent)
                    if (distance > ACTION_CANCEL_TRIGGER_DISTANCE) {
                        motionEvent.action = MotionEvent.ACTION_CANCEL
                        iv.dispatchTouchEvent(motionEvent)
                    }
                }
            }

            return@setOnTouchListener true
        }
    }

    private fun getMoveDistance(startX: Float, startY: Float, ev: MotionEvent): Float {
        val dx = ev.x - startX
        val dy = ev.y - startY
        return sqrt(dx * dx + dy * dy)
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
            binding.rvLocation.visibility = View.GONE
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

            locationAdapter.submitList(it)
            if (it.isNotEmpty()) {
                binding.ivLocationSearch.visibility = View.GONE
                binding.tvLocationInfo.visibility = View.GONE
                binding.rvLocation.visibility = View.VISIBLE
            } else {
                binding.rvLocation.visibility = View.INVISIBLE
                binding.ivLocationSearch.visibility = View.VISIBLE
                binding.tvLocationInfo.text = requireContext().getString(R.string.str_nothing_found)
                binding.tvLocationInfo.visibility = View.VISIBLE
            }
            binding.pbLocationSearch.visibility = View.GONE
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvCurrentLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                binding.tvCurrentLocation.text =
                    requireContext().getString(R.string.str_current_location)
                binding.tvLocationName.text = requireContext().getString(
                    R.string.str_location_name,
                    it.name,
                    it.country
                )
                binding.tvLocationName.visibility = View.VISIBLE
                postDelayTvLocationNameAnimation()
            } else {
                binding.tvLocationName.visibility = View.GONE
                binding.tvCurrentLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                binding.tvCurrentLocation.text =
                    requireContext().getString(R.string.str_undefined_location)
            }
        }

        viewModel.isLocationDetectSuccess.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.str_detect_location_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.str_detect_location_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.hasInternetConnection.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.pbLocationSearch.visibility = View.GONE
                binding.ivLocationSearch.visibility = View.VISIBLE
                binding.tvLocationInfo.visibility = View.VISIBLE
                binding.tvLocationInfo.text =
                    requireContext().getString(R.string.str_location_no_internet)
            }
        }
    }

    private fun searchModeOff() {
        isSearchMode = false
        locationAdapter.submitList(null)
        binding.rvLocation.visibility = View.GONE

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

    private fun requestLocationPermission() {
        if (PermissionsManager.isLocationPermissionGranted(requireContext())) {
            viewModel.onLocationPermissionGranted()
            Toast.makeText(
                requireContext(),
                getString(R.string.str_detecting_location),
                Toast.LENGTH_LONG
            ).show()
        } else {
            showOnLocationPermissionDeniedSnackbar()
        }
    }

    private fun showOnLocationPermissionDeniedSnackbar() {
        snackbar = Snackbar.make(
            binding.fragmentLocation,
            getString(R.string.str_loc_permission_denied_settings),
            Snackbar.LENGTH_LONG
        )
            .setTextMaxLines(4)
            .setAction(getString(R.string.str_settings)) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        snackbar?.show()
    }

    companion object {
        fun newInstance() = LocationFragment()

        private const val ACTION_CANCEL_TRIGGER_DISTANCE = 250
    }
}