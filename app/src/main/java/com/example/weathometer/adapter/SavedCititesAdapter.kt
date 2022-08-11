package com.example.weathometer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weathometer.databinding.SavedCitiesRvItemBinding
import com.example.weathometer.model.CitiesResponse
import com.example.weathometer.model.CitySqlite

class SavedCitiesAdapter(viewClickListener: CityRVClickListener) :  RecyclerView.Adapter<SavedCitiesAdapter.SavedCitiesViewHOlder>() {

    private val listener: CityRVClickListener = viewClickListener


    inner class SavedCitiesViewHOlder(val binding: SavedCitiesRvItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<CitySqlite>() {
        override fun areItemsTheSame(oldItem: CitySqlite, newItem: CitySqlite): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CitySqlite, newItem: CitySqlite): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var cities: List<CitySqlite>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = cities.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCitiesViewHOlder {
        return SavedCitiesViewHOlder(
            SavedCitiesRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: SavedCitiesViewHOlder, position: Int) {
        holder.binding.apply {
            val city = cities[position]
            tvCityRv.text=city.name
            tvTempRv.text=city.temp
            btnDeleteRv.setOnClickListener {
                listener.onDeleteClick(city)
            }



           /* tvTitle.text = todo.title
            cbDone.isChecked = todo.completed*/

        }
    }

    interface CityRVClickListener {
        fun onDeleteClick(citySqlite: CitySqlite)
    }
}


