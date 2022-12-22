package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationsService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    // TODO проверить
    public List<Film> getFilmRecommendations(int userId) {
        List<Integer> userFilms = filmStorage.getUserFilms(userId);
        Map<Integer, Integer> allMatches = userStorage.getUsersWithMatchingFilms(userFilms);
        int maxMatches = Collections.max(allMatches.values());

        Map<Integer, List<Integer>> topMatches = new HashMap<>();

        // TODO возможно, заменить на стрим
        for (Map.Entry<Integer, Integer> entry : allMatches.entrySet()) {
            if (entry.getValue() == maxMatches) {
                List<Integer> likedFilms = filmStorage.getUserFilms(entry.getKey());
                topMatches.put(entry.getKey(), likedFilms);
            }
        }

        // TODO возможно, переписать без циклов
        Set<Integer> recommendedFilmsIds = new HashSet<>();
        for (Map.Entry<Integer, List<Integer>> entry : topMatches.entrySet()) {
            for (Integer filmId : entry.getValue()) {
                if (!userFilms.contains(filmId)) {
                    recommendedFilmsIds.add(filmId);
                }
            }
        }
        return filmStorage.getById(recommendedFilmsIds);
    }

}
