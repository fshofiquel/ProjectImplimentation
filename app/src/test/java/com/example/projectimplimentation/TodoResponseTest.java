package com.example.projectimplimentation;

import com.example.projectimplimentation.network.TodoResponse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TodoResponseTest {

    @Test
    public void todoResponse_holdsServerFields() {
        TodoResponse response = new TodoResponse(1, "delectus aut autem", false);

        assertEquals(1, response.getId());
        assertEquals("delectus aut autem", response.getTitle());
        assertFalse(response.isCompleted());
    }
}
