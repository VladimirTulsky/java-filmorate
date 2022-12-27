package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    private Long eventId;
    private Long entityId;
    private long timestamp;
    private Long userId;
    private EventType eventType;
    private OperationType operation;
}
