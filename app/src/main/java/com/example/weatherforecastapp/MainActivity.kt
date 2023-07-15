package com.example.weatherforecastapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.Adapter.CountriesRecyclerViewAdapter
import com.example.weatherforecastapp.Adapter.WeatherInfoRecyclerViewAdapter
import com.example.weatherforecastapp.Model.CountriesDataModel
import com.example.weatherforecastapp.Model.WeatherInfoDataModel
import com.example.weatherforecastapp.Retrofit.ApiViewModel
import com.example.weatherforecastapp.Room.Entity
import com.example.weatherforecastapp.Room.MyRoomDatabase
import com.example.weatherforecastapp.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(),LocationListener {
    lateinit var binding: ActivityMainBinding
    private lateinit var apiViewModel: ApiViewModel
    var countriesRecyclerViewAdapter: CountriesRecyclerViewAdapter? = null
    var weatherInfoRecyclerViewAdapter: WeatherInfoRecyclerViewAdapter? = null
    private var countriesDataModel: ArrayList<CountriesDataModel>? = ArrayList()
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var weatherInfoDataModel: ArrayList<WeatherInfoDataModel>? = ArrayList()
    lateinit var database: MyRoomDatabase

    private var appid = "1098adf200fe551b3cb4fedf79f47b52"
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        apiViewModel = ViewModelProvider(this)[ApiViewModel::class.java]
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)
        database = MyRoomDatabase.getDatabase(this)

        //get lat long of current location
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,this)
        val myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        if (myLocation != null) {
            checkConnection(myLocation.latitude.toString(),myLocation.longitude.toString())
            /*getCurrentWeatherFromApi(myLocation.latitude.toString(),myLocation.longitude.toString())
            Log.d("Location_Update","Latitude: " + myLocation.latitude + " , Longitude: " + myLocation.longitude)*/
        }

        binding.rootLayout.countriesRecyclerView.layoutManager = object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return true
            }
        }
        countriesDataModel!!.clear()
        weatherInfoDataModel!!.clear()
        binding.rootLayout.countriesRecyclerView.setHasFixedSize(true)
        binding.rootLayout.countriesRecyclerView.isHorizontalScrollBarEnabled = true
        binding.rootLayout.countriesRecyclerView.itemAnimator = DefaultItemAnimator()

        countriesDataModel!!.add(CountriesDataModel("New York","40.7128","74.0060"))
        countriesDataModel!!.add(CountriesDataModel("Singapore","1.3521","103.8198"))
        countriesDataModel!!.add(CountriesDataModel("Mumbai","19.0760","72.8777"))
        countriesDataModel!!.add(CountriesDataModel("Delhi","28.7041","77.1025"))
        countriesDataModel!!.add(CountriesDataModel("Sydney","33.8688","151.2093"))
        countriesDataModel!!.add(CountriesDataModel("Melbourne","37.8136","144.9631"))

        countriesRecyclerViewAdapter =
            CountriesRecyclerViewAdapter(this, countriesDataModel!!)
        binding.rootLayout.countriesRecyclerView.adapter = countriesRecyclerViewAdapter

        countriesRecyclerViewAdapter!!.setOnClickListener(object : CountriesRecyclerViewAdapter.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(position: Int, lat: String, lon: String) {
                checkConnection(lat,lon)
                //getCurrentWeatherFromApi(lat,lon)
            }

        })

        binding.rootLayout.infoIcon.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.rootLayout.resetIcon.setOnClickListener {
            if (myLocation != null) {
                checkConnection(myLocation.latitude.toString(),myLocation.longitude.toString())
                //getCurrentWeatherFromApi(myLocation.latitude.toString(),myLocation.longitude.toString())
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkConnection(lat: String, lon: String): Boolean {
        val connMgr = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connMgr.activeNetworkInfo
        if (activeNetworkInfo != null) {
            // connected to the internet
            //Toast.makeText(this,"Connected to internet",Toast.LENGTH_SHORT).show()
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                getCurrentWeatherFromApi(lat,lon)
                return true
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                getCurrentWeatherFromApi(lat,lon)
                return true
            }
        }
        else{
            // not connect to internet
            Toast.makeText(this,"Internet connectivity issue",Toast.LENGTH_SHORT).show()
            getWeatherFromLocal()
        }
        return false
    }

    private fun getWeatherFromLocal() {
        database.dao().getWeatherDetail().observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                var convertKelvinToCelcius = it.get(0).temp.toDouble()-273.15
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.DOWN
                val convertKelvinToCelsiusRoundOff = df.format(convertKelvinToCelcius)
                binding.rootLayout.currentTemperature.text = convertKelvinToCelsiusRoundOff.toString()
                binding.rootLayout.currentLocationName.text = it.get(0).timezone
                binding.rootLayout.weatherType.text = it.get(0).weather
                binding.rootLayout.lastUpdatedOn.text = "Last updated on "+it.get(0).last_updated_on

                weatherInfoDataModel!!.clear()
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Current temperature",it.get(0).temp))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Current weather",it.get(0).weather))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently feels like",it.get(0).feels_like))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently pressure",it.get(0).pressure))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently humidity",it.get(0).humidity))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently dew point",it.get(0).dew_point))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently UVI",it.get(0).uvi))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently wind speed",it.get(0).wind_speed))
                weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently wind deg",it.get(0).wind_deg))

                binding.bottomSheet.weatherInfoRecyclerView.layoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false) {
                    override fun canScrollVertically(): Boolean {
                        return true
                    }

                }
                binding.bottomSheet.weatherInfoRecyclerView.setHasFixedSize(true)
                binding.bottomSheet.weatherInfoRecyclerView.isHorizontalScrollBarEnabled = true
                binding.bottomSheet.weatherInfoRecyclerView.itemAnimator = DefaultItemAnimator()

                weatherInfoRecyclerViewAdapter =
                    WeatherInfoRecyclerViewAdapter(this, weatherInfoDataModel!!)
                binding.bottomSheet.weatherInfoRecyclerView.adapter = weatherInfoRecyclerViewAdapter
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentWeatherFromApi(lat: String, lon: String) {

        apiViewModel.setDataToLoadWeather(lat,lon,appid)
        apiViewModel.getCurrentWeatherData().observe(this){
            var convertKelvinToCelcius = it.current.temp-273.15
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            val convertKelvinToCelsiusRoundOff = df.format(convertKelvinToCelcius)
            binding.rootLayout.currentTemperature.text = convertKelvinToCelsiusRoundOff.toString()
            binding.rootLayout.currentLocationName.text = it.timezone
            binding.rootLayout.weatherType.text = it.current.weather.get(0).main

            weatherInfoDataModel!!.clear()
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Current temperature",it.current.temp.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Current weather",it.current.weather[0].main))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently feels like",it.current.feels_like.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently pressure",it.current.pressure.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently humidity",it.current.humidity.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently dew point",it.current.dew_point.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently UVI",it.current.uvi.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently wind speed",it.current.wind_speed.toString()))
            weatherInfoDataModel!!.add(WeatherInfoDataModel("Currently wind deg",it.current.wind_deg.toString()))

            binding.bottomSheet.weatherInfoRecyclerView.layoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false) {
                override fun canScrollVertically(): Boolean {
                    return true
                }

            }
            binding.bottomSheet.weatherInfoRecyclerView.setHasFixedSize(true)
            binding.bottomSheet.weatherInfoRecyclerView.isHorizontalScrollBarEnabled = true
            binding.bottomSheet.weatherInfoRecyclerView.itemAnimator = DefaultItemAnimator()

            weatherInfoRecyclerViewAdapter =
                WeatherInfoRecyclerViewAdapter(this, weatherInfoDataModel!!)
            binding.bottomSheet.weatherInfoRecyclerView.adapter = weatherInfoRecyclerViewAdapter

            var currentTimestamp = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = currentTimestamp.format(formatter)

            binding.rootLayout.lastUpdatedOn.text = "Last updated on " + formatted

            GlobalScope.launch {
                database.clearAllTables()
                database.dao().insertWeatherData(
                    Entity(0,lat,lon,it.timezone,it.current.weather[0].main,it.current.temp.toString(),it.current.feels_like.toString(),it.current.pressure.toString(),it.current.humidity.toString(),
                    it.current.dew_point.toString(),it.current.uvi.toString(),it.current.clouds.toString(),it.current.wind_speed.toString(),it.current.wind_deg.toString(),formatted))
            }


        }
    }

    override fun onLocationChanged(location: Location) {
        //Log.d("Location_Update","Latitude: " + location.latitude + " , Longitude: " + location.longitude)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
