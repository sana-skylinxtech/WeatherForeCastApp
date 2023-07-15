package com.example.weatherforecastapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapp.Model.WeatherInfoDataModel
import com.example.weatherforecastapp.R

class WeatherInfoRecyclerViewAdapter(context: Context, allInfoList: ArrayList<WeatherInfoDataModel>) :
    RecyclerView.Adapter<WeatherInfoRecyclerViewAdapter.MyViewHolder?>() {
    private var mListener: OnItemClickListener? = null
    private  var allInfoList: ArrayList<WeatherInfoDataModel> = allInfoList
    private var context: Context? = context

    interface OnItemClickListener {
        fun onItemClick(position: Int, stateDescription: String?, expandedCount: Int)
    }

    fun setOnClickListener(mListener: OnItemClickListener?) {
        this.mListener = mListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): WeatherInfoRecyclerViewAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_info_recyclerview_item, parent, false)
        return WeatherInfoRecyclerViewAdapter.MyViewHolder(view, mListener)
    }


    override fun onBindViewHolder(holder: WeatherInfoRecyclerViewAdapter.MyViewHolder, position: Int) {
        var infoName: String = allInfoList[position].infoName
        holder.infoName!!.text = infoName
        var infoValue: String = allInfoList[position].infoValue
        holder.infoValue!!.text = infoValue

    }

    override fun getItemCount(): Int {
        return allInfoList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var infoName: TextView? = null
        var infoValue: TextView? = null

        init{
            infoName = itemView.findViewById(R.id.infoName)
            infoValue = itemView.findViewById(R.id.infoValue)
        }
    }
}