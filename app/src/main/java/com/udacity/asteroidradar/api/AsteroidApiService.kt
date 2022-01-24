package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import okhttp3.OkHttpClient
// import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.scalars.ScalarsConverterFactory

import java.util.concurrent.TimeUnit




    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
     * object.
     */


    /**
     * A public interface that exposes the [getProperties] method
     */
    interface AsteroidApiService {
        /**
         * Returns a Coroutine [List] of [MarsProperty] which can be fetched with await() if in a Coroutine scope.
         * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
         * HTTP method
         */
        @GET("neo/rest/v1/feed")
        suspend fun getAsteroids(
            @Query("start_date") startDate: String,
            @Query("end_date") endDate: String,
            @Query("api_key") apiKey: String
        ): String

        @GET("planetary/apod")
        suspend fun getPictureOfTheDay(
            @Query("api_key") api_key: String = Constants.API_KEY
        ): PictureOfDay



        }

    /**
     * A public Api object that exposes the lazy-initialized Retrofit service
     */

   object Network{
        private val okhttpclient = OkHttpClient()
        var eagerClient = okhttpclient.newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(1,TimeUnit.MINUTES)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(eagerClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val service: AsteroidApiService = retrofit.create(AsteroidApiService::class.java)
   }




