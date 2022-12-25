package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    private Integer eventId;
    private Integer entityId;
    private long timestamp;
    private Integer userId;
    private EventType eventType;
    private OperationType operation;
}
