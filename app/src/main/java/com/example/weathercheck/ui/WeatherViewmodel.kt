package com.example.weathercheck.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercheck.data.model.Weather
import com.example.weathercheck.data.model.WeatherData
import com.example.weathercheck.data.model.WeatherInfoResponse
import com.example.weathercheck.domain.WeatherRepo
import com.example.weathercheck.utils.kelvinToCelsius
import com.example.weathercheck.utils.unixTimestampToDateTimeString
import com.example.weathercheck.utils.unixTimestampToTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel
@Inject
constructor(private val repository: WeatherRepo) : ViewModel() {

    private val _response = MutableLiveData<WeatherData>()
    val weatherResponse: LiveData<WeatherData>
        get() = _response

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean>
        get() = _progress

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getWeather(query: String) = viewModelScope.launch {
        _progress.postValue(true)
        repository.getWeather(query).let { response ->
            if (response.isSuccessful && !response.equals(null)) {
                val data = response.body()
                val weatherData = WeatherData(
                    dateTime = data?.dt!!.unixTimestampToDateTimeString(),
                    temperature = data.main.temp.kelvinToCelsius().toString(),
                    cityAndCountry = "${data.name}, ${data.sys.country}",
                    weatherConditionIconUrl = "http://openweathermap.org/img/w/${data.weather[0].icon}.png",
                    weatherConditionIconDescription = data.weather[0].description,
                    humidity = "${data.main.humidity}%",
                    pressure = "${data.main.pressure} mBar",
                    visibility = "${data.visibility/1000.0} KM",
                    sunrise = data.sys.sunrise.unixTimestampToTimeString(),
                    sunset = data.sys.sunset.unixTimestampToTimeString()
                )
                _response.postValue(weatherData)
                _progress.postValue(false)
            } else {
                if (response.code() == 404) {
                    _error.postValue("City Not Found")
                } else {
                    Log.d("tag", "getWeather Error: ${response.code()}")
                }
                _progress.postValue(false)
            }
        }
    }
}