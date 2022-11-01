package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {

    @NotNull
    private int id;
    @NotBlank(message = "Некорректное название фильма")
    private final String name;
    @NotNull(message = "Отсутствует описание фильма")
    @Size(min = 1, max = 200, message = "Описание превышает максимальный размер(200символов)")
    private final String description;
    private final LocalDate releaseDate;
    @Min(value = 1, message = "Некорректная продолжительность фильма")
    private final long duration;

}
