package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @PositiveOrZero
    private long id;
    @NotBlank(message = "Некорректное название фильма")
    @Size(max = 60, message = "Слишком длинное название фильма")
    private String name;
    @NotNull(message = "Отсутствует описание фильма")
    @Size(min = 1, max = 200, message = "Описание превышает максимальный размер(200символов)")
    private String description;
    private LocalDate releaseDate;
    @Min(value = 1, message = "Некорректная продолжительность фильма")
    private long duration;
    private Mpa mpa;
    private LinkedHashSet<Genre> genres;
    private LinkedHashSet<Director> directors;
}
