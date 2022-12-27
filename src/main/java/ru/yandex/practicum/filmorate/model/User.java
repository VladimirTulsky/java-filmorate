package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @PositiveOrZero
    private long id;
    @NotBlank(message = "Отсутствует email")
    @Email(message = "Некорректный email")
    @Size(max = 50)
    private String email;
    @NotNull(message = "Отсутствует логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    @Size(min = 1, max = 20)
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;
}
