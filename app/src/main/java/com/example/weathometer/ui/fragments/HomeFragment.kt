package com.example.weathometer.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.stuplan.sqlite.DatabaseHelper
import com.example.weathometer.R
import com.example.weathometer.Utility.PreferenceMangager
import com.example.weathometer.databinding.FragmentHomeBinding
import com.example.weathometer.model.CitiesResponse
import com.example.weathometer.model.CitySqlite
import com.example.weathometer.network.API_KEY
import com.example.weathometer.network.RetrofitInstance
import com.example.weathometer.network.unit
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

const val TAG = "HomeFragment"


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var cityResponse:CitiesResponse
    private lateinit var dbHelper : DatabaseHelper
    var cityList = ArrayList<CitySqlite>()
    private lateinit var preferenceMangager: PreferenceMangager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSaveCity.isVisible=false
        dbHelper = DatabaseHelper(requireContext())
        preferenceMangager = PreferenceMangager(requireContext())

        val location = preferenceMangager.getString(PreferenceMangager.DEFAULT_CITY)
        binding.tvCity.text=location
        callserver(location)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callserver(location : String) {
        hideSaveCityTV(false)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            binding.progressBar.isVisible = true
            val response = try {
                RetrofitInstance.api.getWeather(location, unit, API_KEY)
            } catch(e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                binding.progressBar.isVisible = false
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                binding.progressBar.isVisible = false
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null) {
                cityResponse=response.body()!!
                println(response.body())
                binding.plGrps.visibility=View.VISIBLE

            } else {
                Log.e(TAG, "Response not successful")
                Toast.makeText(requireContext(), "Response Not Successful", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.isVisible = false

            val currentDate: String = SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault()).format(Date())
            binding.tvDate.text=currentDate

            val fadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            val slideFromRight: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
            val slideFromLeft: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)

            val url="http://openweathermap.org/img/wn/${cityResponse.weather[0].icon}.png"
            println("img url: $url")

            Glide
                .with(this@HomeFragment)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_weather)
                .into(binding.ivWeather)

            binding.tvWeather.also {
                it.text=cityResponse.weather[0].main
                it.startAnimation(fadeIn)
            }

            binding.tvHumidityPlaceholder.startAnimation(slideFromLeft)
            binding.tvHumidity.also {
                it.text="${cityResponse.main.humidity}%"
                it.startAnimation(slideFromRight)
            }

            binding.tvTemperature.also {
                it.text="${cityResponse.main.temp}"+"°C"
                it.startAnimation(fadeIn)

            }

            binding.tvMinTempPlaceholder.startAnimation(slideFromLeft)

            binding.tvMinTemp.also {
                it.startAnimation(slideFromRight)
                it.text="${cityResponse.main.temp_min}"+"°C"
            }

            binding.tvMaxTempPlaceholder.startAnimation(slideFromLeft)

            binding.tvMaxTemp.also {
                it.startAnimation(slideFromRight)
                it.text="${cityResponse.main.temp_max}"+"°C"
            }

            binding.tvPressurePlaceholder.startAnimation(slideFromLeft)

            binding.tvPressure.also {
                it.startAnimation(slideFromRight)
                it.text="${cityResponse.main.pressure}"+"hPa"
            }

            binding.tvCity.setOnClickListener {
                changeCity()
            }

            binding.tvSaveCity.setOnClickListener {
                //hideSaveCityTV(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    val city = CitySqlite(null,binding.tvCity.text.toString(),"${cityResponse.main.temp}"+"°C")
                    val sqliteResponse=dbHelper.insertCity(city)

                    if (sqliteResponse>-1){
                        city.id=sqliteResponse
                        Toast.makeText(requireContext(), "City Saved", Toast.LENGTH_SHORT).show()
                        hideSaveCityTV(true)
                    }
                }
            }
            return@launchWhenCreated
        }

    }

    private fun hideSaveCityTV(b: Boolean) {
        binding.tvSaveCity.isInvisible = b
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeCity() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .create()
        val dialogView = layoutInflater.inflate(R.layout.change_city_ui,null)
        builder.setView(dialogView)
        
        val etChangeCity = dialogView.findViewById<EditText>(R.id.et_change_city)
        val btnChangeCity = dialogView.findViewById<Button>(R.id.btn_change_city)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel_change_city)

        btnChangeCity.setOnClickListener {
            val cityNameFromET = etChangeCity.text.toString()
            if (cityNameFromET.isNullOrEmpty()){
                Toast.makeText(requireContext(), "City name is empty.", Toast.LENGTH_SHORT).show()
            }else{
                binding.tvCity.text=etChangeCity.text.toString()
                preferenceMangager.putString(PreferenceMangager.DEFAULT_CITY,cityNameFromET)
                callserver(cityNameFromET)

            }
            builder.dismiss()
        }

        btnCancel.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}