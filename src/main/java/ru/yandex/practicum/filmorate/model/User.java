package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {

    @NotNull
    private int id;
    @Email(message = "Некорректный email")
    private final String email;
    @NotNull(message = "Отсутствует логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private final String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    private final LocalDate birthday;

}
