package com.hermanbocharov.weatherforecast.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentLocationBinding
import com.hermanbocharov.weatherforecast.domain.Location
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
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

    private var disposable: Disposable? = null
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
        disposable?.dispose()
        _binding = null
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter()
        binding.rvLocation.adapter = locationAdapter

        locationAdapter.onLocationClickListener = {
            disposable?.dispose()
            binding.tvLocationName.isSelected = false
            viewModel.addNewLocation(it)
            locationAdapter.submitList(listOf<Location>())
        }
    }

    private fun setupSearchView() {
        binding.svLocation.apply {
            setBackgroundResource(R.drawable.search_view_bg)
            //isIconified = false
            //isSubmitButtonEnabled = true

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(input: String?): Boolean {
                    if (input.isNullOrBlank()) {
                        return false
                    }

                    viewModel.getListOfCities(input)
                    return true
                }

                override fun onQueryTextChange(input: String?): Boolean {
                    return true
                }

            })
        }
    }

    private fun observeViewModel() {
        viewModel.listOfCities.observe(viewLifecycleOwner) {
            locationAdapter.submitList(it)
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

    private fun postDelayTvLocationNameAnimation() {
        disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.tvLocationName.isSelected = true
            }
    }

    companion object {

        fun newInstance() = LocationFragment()
    }
}