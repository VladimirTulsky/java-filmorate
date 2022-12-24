package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Review {
    private int reviewId;
    private String content;
    private Boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;
}
