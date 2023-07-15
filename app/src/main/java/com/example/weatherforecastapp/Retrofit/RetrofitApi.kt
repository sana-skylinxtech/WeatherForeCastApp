package com.example.weatherforecastapp.Retrofit

import com.example.weatherforecastapp.Model.GetWeatherResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi {

    @GET
    fun getCurrentWeather(@Url url:String): Call<GetWeatherResponseModel>

}