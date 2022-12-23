package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class GenresControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    public GenresControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getGenreByIdTest() throws Exception {
        mockMvc.perform(
                        get("/genres/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Драма"));
    }

    @Test
    void getAllGenresTest() throws Exception {
        String jsonForCheck = "[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}," +
                "{\"id\":3,\"name\":\"Мультфильм\"},{\"id\":4,\"name\":\"Триллер\"}," +
                "{\"id\":5,\"name\":\"Документальный\"},{\"id\":6,\"name\":\"Боевик\"}]";

        mockMvc.perform(
                        get("/genres")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(jsonForCheck, result.getResponse().getContentAsString(StandardCharsets.UTF_8)));
    }
}
