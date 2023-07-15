package com.example.weatherforecastapp.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecastapp.Model.Current

@Entity(tableName = "current_weather")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val lat: String,
    val lon: String,
    val timezone: String,
    val weather: String,
    val temp: String,
    val feels_like: String,
    val pressure: String,
    val humidity: String,
    val dew_point: String,
    val uvi: String,
    val clouds: String,
    val wind_speed: String,
    val wind_deg: String,
    val last_updated_on: String
)