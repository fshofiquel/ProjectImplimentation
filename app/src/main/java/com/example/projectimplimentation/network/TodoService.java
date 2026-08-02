package com.example.projectimplimentation.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TodoService {
    // Read a small window of REST TODO items for display.
    @GET("todos")
    Call<List<TodoResponse>> getTodos(@Query("_limit") int limit);

    // Upload a local TODO item to REST side.
    @POST("todos")
    Call<TodoResponse> uploadTodo(@Body TodoUploadRequest request);
}
