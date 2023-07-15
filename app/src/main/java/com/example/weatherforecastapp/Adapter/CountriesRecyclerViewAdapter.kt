package com.example.weatherforecastapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapp.Model.CountriesDataModel
import com.example.weatherforecastapp.R

class CountriesRecyclerViewAdapter(context: Context, allCountriesList: ArrayList<CountriesDataModel>) :
    RecyclerView.Adapter<CountriesRecyclerViewAdapter.MyViewHolder?>() {
    private var mListener: OnItemClickListener? = null
    private  var allCountriesList: ArrayList<CountriesDataModel> = allCountriesList
    private var context: Context? = context

    interface OnItemClickListener {
        fun onItemClick(position: Int, lat: String, lon: String)
    }

    fun setOnClickListener(mListener: OnItemClickListener?) {
        this.mListener = mListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): CountriesRecyclerViewAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.countries_recyclerview_item, parent, false)
        return CountriesRecyclerViewAdapter.MyViewHolder(view, mListener)
    }


    override fun onBindViewHolder(holder: CountriesRecyclerViewAdapter.MyViewHolder, position: Int) {
        var countryName: String = allCountriesList[position].countryName
        holder.countryName!!.text = countryName
        var countryLat: String = allCountriesList[position].countryLat
        holder.countryLat!!.text = countryLat
        var countryLon: String = allCountriesList[position].countryLon
        holder.countryLon!!.text = countryLon

        holder.itemView.setOnClickListener {
            mListener!!.onItemClick(position,allCountriesList[position].countryLat,allCountriesList[position].countryLon)
        }

    }

    override fun getItemCount(): Int {
        return allCountriesList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var countryName: TextView? = null
        var countryLat: TextView? = null
        var countryLon: TextView? = null

        init{
            countryName = itemView.findViewById(R.id.countryName)
            countryLat = itemView.findViewById(R.id.countryLat)
            countryLon = itemView.findViewById(R.id.countryLon)
        }
    }
}