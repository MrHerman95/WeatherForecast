package com.hermanbocharov.weatherforecast.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.FragmentLocationBinding
import com.hermanbocharov.weatherforecast.domain.Location

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentLocationBinding is null")

    private val viewModel: LocationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FRAGMENTS", "onCreate() LocationFragment")
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter()
        binding.rvLocation.adapter = locationAdapter

        locationAdapter.onLocationClickListener = {
            binding.tvCurrentCity.text = requireContext().getString(
                R.string.str_current_city,
                it.name
            )

            viewModel.addNewLocation(it)

            locationAdapter.submitList(listOf<Location>())
        }
    }

    private fun setupSearchView() {
        binding.svLocation.apply {
            isIconifiedByDefault = false
            isSubmitButtonEnabled = true

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
            binding.tvCurrentCity.text = requireContext().getString(
                R.string.str_current_city,
                it.name
            )
        }
    }

    companion object {

        fun newInstance() = LocationFragment()
    }
}