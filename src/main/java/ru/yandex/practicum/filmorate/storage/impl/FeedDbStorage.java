package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFeed(Long entityId, Long userId, long timeStamp,
                        EventType eventType, OperationType operation) {
        String sqlQuery = "insert into events (entity_id, user_Id, time_stamp, event_type, event_operation) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, entityId, userId, timeStamp, eventType.toString(), operation.toString());
    }

    @Override
    public List<Feed> getFeed(long id) {
        String sqlGetFeed = "select * from events where user_id = ? order by EVENT_ID";
        return jdbcTemplate.query(sqlGetFeed, this::makeFeed, id);
    }

    Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        long eventId = rs.getLong("event_id");
        long entityId = rs.getLong("entity_id");
        long userId = rs.getLong("user_id");
        long time = rs.getLong("time_stamp");
        EventType eventType = EventType.valueOf(rs.getString("event_type"));
        OperationType operation = OperationType.valueOf(rs.getString("event_operation"));
        return new Feed(eventId, entityId, time, userId, eventType, operation);
    }
}
