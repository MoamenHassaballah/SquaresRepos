package com.moaapps.squaresrepos.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.moaapps.squaresrepos.BuildConfig
import com.moaapps.squaresrepos.api.API
import com.moaapps.squaresrepos.utils.InternetConnection.hasNetwork
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InitRetrofit(val context: Context) {
    private fun getRetrofit(): Retrofit {
        val cashSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cashSize)
        val okHttpClient = OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor {
                var request = it.request()
                request = if (!hasNetwork(context)!!){
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 10).build()
                }else{
                    request.newBuilder().build()
                }
                it.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val retrofit = getRetrofit().create(API::class.java)



}