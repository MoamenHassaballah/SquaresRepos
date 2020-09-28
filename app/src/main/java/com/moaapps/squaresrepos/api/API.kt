package com.moaapps.squaresrepos.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("repos")
    suspend fun getRepos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<Any>
}