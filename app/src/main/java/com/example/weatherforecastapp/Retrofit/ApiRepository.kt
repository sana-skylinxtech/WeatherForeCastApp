package com.example.weatherforecastapp.Retrofit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecastapp.Model.GetWeatherResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {

    var apiInterface: RetrofitApi = RetrofitHelper.createService(RetrofitApi::class.java)

    companion object{
        fun getInstance(): ApiRepository {
            val apiRepository: ApiRepository by lazy {
                ApiRepository()
            }
            return apiRepository
        }
    }

    private var loadWeatherDataSet = MutableLiveData<GetWeatherResponseModel>()
    fun setDataToLoadWeather(lat:String, lon: String, appid:String): MutableLiveData<GetWeatherResponseModel>{
        apiInterface.getCurrentWeather("onecall?lat=$lat&lon=$lon&appid=$appid").enqueue(object : Callback<GetWeatherResponseModel>{
            override fun onResponse(
                call: Call<GetWeatherResponseModel>,
                response: Response<GetWeatherResponseModel>
            ) {
                if (response.isSuccessful) {
                    loadWeatherDataSet.value = response.body()
                    Log.d("status_success", response.body().toString())
                }
                else {
                    var json:String = response.errorBody()!!.string()
                    //loadWeatherDataSet.value?.message = json
                    Log.d("status_error", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<GetWeatherResponseModel>, t: Throwable) {
                Log.d("status_failure", t.message.toString())
            }

        })
        return loadWeatherDataSet
    }
}