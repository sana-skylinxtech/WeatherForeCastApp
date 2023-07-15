package com.example.weatherforecastapp.Model

class WeatherInfoDataModel {
    var infoName: String = ""
    var infoValue: String = ""

    constructor(infoName: String, infoValue: String) {
        this.infoName = infoName
        this.infoValue = infoValue
    }
}