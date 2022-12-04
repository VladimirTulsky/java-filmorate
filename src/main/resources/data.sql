MERGE INTO GENRE (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO GENRE (genre_id, name) VALUES (2, 'Драма');
MERGE INTO GENRE (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO GENRE (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO GENRE (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO GENRE (genre_id, name) VALUES (6, 'Боевик');

MERGE INTO MPA (id, name) VALUES (1, 'G');
MERGE INTO MPA (id, name) VALUES (2, 'PG');
MERGE INTO MPA (id, name) VALUES (3, 'PG-13');
MERGE INTO MPA (id, name) VALUES (4, 'R');
MERGE INTO MPA (id, name) VALUES (5, 'NC-17');

-- MERGE INTO FILMS (id, name, description, release_date, duration)
-- VALUES (1, 'Film 1', 'film 1', CAST('2020-05-05' AS date), 120);
--
-- MERGE INTO FILMS (id, name, description, release_date, duration)
-- VALUES (2,'Film 2', 'film 2', CAST('2020-05-05' AS date), 111);
--
-- MERGE INTO USERS (id, email, login, name, birthday)
-- VALUES (1, 'test@test.com', 'login', 'name', CAST('1992-05-05' AS date));
--
-- MERGE INTO USERS (id, email, login, name, birthday)
-- VALUES (2, 'test2@test.com', 'login2', 'name2', CAST('1993-05-05' AS date));