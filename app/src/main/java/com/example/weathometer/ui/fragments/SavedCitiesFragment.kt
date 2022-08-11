package com.example.weathometer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stuplan.sqlite.DatabaseHelper
import com.example.weathometer.adapter.SavedCitiesAdapter
import com.example.weathometer.databinding.FragmentDashboardBinding
import com.example.weathometer.model.CitySqlite
import java.util.ArrayList

class SavedCitiesFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var citiesAdapter: SavedCitiesAdapter
    private lateinit var dbHelper : DatabaseHelper
    var cityList = ArrayList<CitySqlite>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        cityList = dbHelper.getAllCity()
        setupRecyclerView()


    }

    private fun setupRecyclerView() = binding.rvSavedCities.apply {
        citiesAdapter = SavedCitiesAdapter(object : SavedCitiesAdapter.CityRVClickListener{
            override fun onDeleteClick(citySqlite: CitySqlite) {
                dbHelper.deleteCity(citySqlite)

                cityList.remove(citySqlite)
                citiesAdapter.notifyDataSetChanged()

                //citiesAdapter.cities = cityList

            }

        } )
        citiesAdapter.cities = cityList
        adapter = citiesAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}