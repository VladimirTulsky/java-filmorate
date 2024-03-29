package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Review {

    @PositiveOrZero
    private long reviewId;
    @NotBlank(message = "Отсутствует текст отзыва")
    @Size(max = 200, message = "Слишком длинный отзыв")
    private String content;
    private Boolean isPositive;
    @NotNull(message = "Необходимо указать пользователя")
    private long userId;
    @NotNull(message = "Необходимо указать фильм")
    private long filmId;
    private int useful;

}
