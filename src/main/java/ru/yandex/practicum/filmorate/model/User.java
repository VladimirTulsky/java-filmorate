package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Set<Integer> friends = new HashSet<>();
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Отсутствует email")
    @Email(message = "Некорректный email")
    @Size(max = 20)
    private final String email;
    @NotNull(message = "Отсутствует логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    @Size(min = 1, max = 20)
    private final String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private final LocalDate birthday;

}
