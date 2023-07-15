package com.example.weatherforecastapp.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Entity::class], version = 2)
abstract class MyRoomDatabase : RoomDatabase(){

    abstract fun dao() : Dao

    companion object{
        @Volatile
        private var INSTANCE : MyRoomDatabase?= null

        fun getDatabase(context: Context) : MyRoomDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        MyRoomDatabase::class.java,
                        "database").fallbackToDestructiveMigration().build()
                }
            }

            return INSTANCE!!
        }
    }
}