package com.example.eltiempo.data

import com.example.eltiempo.Networks.ApiService
import com.example.eltiempo.Networks.RetrofitHelper

class Repository {

    private val apiService = RetrofitHelper.getRetrofit()

    suspend fun tiempo(lat:Double,lon:Double,appid:String,lang:String,units:String) = apiService.tiempo(lat,lon,appid,lang,units)
}