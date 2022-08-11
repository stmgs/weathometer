package com.example.weathometer.network

import com.example.weathometer.model.CitiesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY="4fcf4c059b8449a613174ea2e84f3e88"
const val unit="metric"

interface WeatherApi {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") location:String,
        @Query("units") metric:String,
        @Query("appid") appid:String,
        ): Response<CitiesResponse>
}