package com.example.weatherforecastapp.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weatherforecastapp.Model.WeatherInfoDataModel

@Dao
interface Dao {
    @Insert
    suspend fun insertWeatherData(entity: Entity)

    @Update
    suspend fun updateWeatherData(entity: Entity)

    @Query("SELECT * FROM CURRENT_WEATHER")
    fun getWeatherDetail() : LiveData<List<Entity>>
}