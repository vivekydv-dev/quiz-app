package com.vivek.practicalroundquizapp

import com.vivek.practicalroundquizapp.model.ApiData
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("api.php?amount=10&type=multiple")
    fun getApiData(): Call<ApiData>
}