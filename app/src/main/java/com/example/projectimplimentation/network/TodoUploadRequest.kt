package com.example.projectimplimentation.network

data class TodoUploadRequest(
    val title: String,
    val completed: Boolean,
    val userId: Int = 1
)
