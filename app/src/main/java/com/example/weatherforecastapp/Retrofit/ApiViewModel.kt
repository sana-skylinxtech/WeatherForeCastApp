package com.example.weatherforecastapp.Retrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecastapp.Model.GetWeatherResponseModel

class ApiViewModel: ViewModel()   {

    private var mutableLiveDataToLoadWeather = MutableLiveData<GetWeatherResponseModel>()
    fun setDataToLoadWeather(lat: String, lon: String, appid: String) {
        mutableLiveDataToLoadWeather = ApiRepository.getInstance().setDataToLoadWeather(lat,lon,appid)
    }
    fun getCurrentWeatherData(): LiveData<GetWeatherResponseModel> {
        return mutableLiveDataToLoadWeather
    }
}