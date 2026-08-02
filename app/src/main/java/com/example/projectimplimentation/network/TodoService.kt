package com.example.projectimplimentation.network

import retrofit2.http.GET
import retrofit2.http.Query

interface TodoService {
    @GET("todos")
    suspend fun getTodos(@Query("_limit") limit: Int = 10): List<TodoResponse>
}
