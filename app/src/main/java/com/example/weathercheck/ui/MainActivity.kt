package com.example.weathercheck.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weathercheck.R
import com.example.weathercheck.data.model.WeatherData
import com.example.weathercheck.databinding.ActivityMainBinding
import com.example.weathercheck.utils.NetworkHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel

    @Inject
    lateinit var networkHelper:NetworkHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        setLiveDataListeners()
        setUpListeners()
    }

    private fun setUpListeners() {

        binding.searchButton.setOnClickListener {
            val searchQuery = binding.editTextSearch.text
            if (!TextUtils.isEmpty(searchQuery)) {
                if (networkHelper.isNetworkConnected()) {
                    viewModel.getWeather(searchQuery.toString())
                } else {
                    Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show() }
            } else {
                Toast.makeText(this, "City Name cannot be Empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLiveDataListeners() {
        viewModel.weatherResponse.observe(this, Observer { weatherData ->
            setWeatherInfo(weatherData)
        })

        viewModel.error.observe(this, Observer { status ->
            Toast.makeText(this,status,Toast.LENGTH_SHORT).show()
            binding.tvWeatherCheck.visibility = View.VISIBLE
        })

        viewModel.progress.observe(this) { status ->
            if (status == true) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvWeatherCheck.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun setWeatherInfo(weatherData: WeatherData) {
        binding.apply {
            tvDateTime.text = weatherData.dateTime
            tvTemperature.text = weatherData.temperature
            tvCityCountry.text = weatherData.cityAndCountry
            tvUnit.visibility = View.VISIBLE
            editTextSearch.text.clear()
            editTextSearch.clearFocus()

            Glide.with(this@MainActivity)
                .load(weatherData.weatherConditionIconUrl)
                .apply(RequestOptions())
                .error(R.drawable.cloudy)
                .into(ivWeatherCondition)

            tvWeatherCondition.text = weatherData.weatherConditionIconDescription
            }
        }
    }
