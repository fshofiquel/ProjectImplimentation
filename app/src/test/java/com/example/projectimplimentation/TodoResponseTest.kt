package com.example.projectimplimentation

import com.example.projectimplimentation.network.TodoResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TodoResponseTest {

    @Test
    fun todoResponse_holdsServerFields() {
        val response = TodoResponse(id = 1, title = "delectus aut autem", completed = false)

        assertEquals(1, response.id)
        assertEquals("delectus aut autem", response.title)
        assertFalse(response.completed)
    }
}
