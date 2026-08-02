package com.example.projectimplimentation.network

import retrofit2.http.GET

interface TodoService {
    @GET("todos/1")
    suspend fun getSampleTodo(): TodoResponse
}
