package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.DataAccessException;

public class DataException extends DataAccessException {
    public DataException(String message) {
        super(message);
    }
}