package com.example.weatherforecastapp.Model

class CountriesDataModel {
    var countryName: String = ""
    var countryLat: String = ""
    var countryLon: String = ""

    constructor(countryName: String, countryLat: String, countryLon: String) {
        this.countryName = countryName
        this.countryLat = countryLat
        this.countryLon = countryLon
    }
}