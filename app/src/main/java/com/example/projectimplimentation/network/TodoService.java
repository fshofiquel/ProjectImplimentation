package com.example.projectimplimentation.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TodoService {
    @GET("todos")
    Call<List<TodoResponse>> getTodos(@Query("_limit") int limit);

    @POST("todos")
    Call<TodoResponse> uploadTodo(@Body TodoUploadRequest request);
}
