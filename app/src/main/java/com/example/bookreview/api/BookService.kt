package com.example.bookreview.api

import com.example.bookreview.model.BestSellerDto
import com.example.bookreview.model.SearchBooksDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {

    @GET("/api/bestSeller.api?categoryId=100&output=json")
    fun getBestSeller(@Query("key") apiKey: String): Call<BestSellerDto>

    @GET("/api/search.api?output=json")
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ): Call<SearchBooksDto>
}