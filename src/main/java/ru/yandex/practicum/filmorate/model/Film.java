package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {

    @NonNull
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final long duration;

}
