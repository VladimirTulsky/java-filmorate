CREATE TABLE IF NOT EXISTS genre (
    genre_id        int PRIMARY KEY,
    name            varchar(20) not null
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id          int not null PRIMARY KEY,
    name            varchar(6) not null
);

CREATE TABLE IF NOT EXISTS users (
    user_id         bigint generated by default as identity primary key,
    email           varchar(50) not null,
    login           varchar(20) not null,
    name            varchar(50),
    birthday        date not null
);

CREATE TABLE IF NOT EXISTS films (
    film_id         bigint generated by default as identity primary key,
    name            varchar(100) not null,
    description     varchar(200) not null,
    release_date    date not null,
    duration        int not null,
    mpa_id          int not null,
    CONSTRAINT fk_mpa_id
        FOREIGN KEY (mpa_id)
            REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id         bigint not null,
    friend_id       bigint not null,
    constraint "friendship_pk"
        PRIMARY KEY (user_id, friend_id),
    constraint "friendship_user_id"
        FOREIGN KEY (user_id)
            REFERENCES users(user_id) ON DELETE CASCADE,
    constraint "friendship_friend_id"
        FOREIGN KEY (friend_id)
            REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS films_likes (
    film_id         bigint not null,
    user_id         bigint not null,
    constraint "films_likes"
        PRIMARY KEY (film_id, user_id),
    constraint "films_likes_film_id"
        FOREIGN KEY (film_id)
            REFERENCES films(film_id) ON DELETE CASCADE,
    constraint "films_likes_user_id"
        FOREIGN KEY (user_id)
            REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id         bigint not null,
    genre_id        int not null,
    constraint "film_genre"
        PRIMARY KEY (film_id, genre_id),
    constraint "film_genre_film_id"
        FOREIGN KEY (film_id)
            REFERENCES films(film_id) ON DELETE CASCADE,
    constraint "film_genre_genre_id"
        FOREIGN KEY (genre_id)
            REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id     bigint generated by default as identity primary key,
    name            varchar(20) UNIQUE not null
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id         bigint not null,
    director_id     bigint not null,
    constraint "film_director"
        PRIMARY KEY (film_id, director_id),
    constraint "film_director_film_id"
        FOREIGN KEY (film_id)
            REFERENCES films(film_id) ON DELETE CASCADE,
    constraint "film_director_director_id"
        FOREIGN KEY (director_id)
            REFERENCES directors(director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id       bigint generated by default as identity primary key,
    content         varchar(200) not null,
    is_positive     boolean,
    user_id         bigint not null,
    film_id         bigint not null,
    useful          int not null,
    constraint "reviews_user_id"
        FOREIGN KEY (user_id)
            REFERENCES users(user_id) ON DELETE CASCADE,
    constraint "reviews_film_id"
        FOREIGN KEY (film_id)
            REFERENCES films(film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    review_id       bigint not null,
    user_id         bigint not null,
    is_positive     boolean,
    constraint "reviews_likes"
        PRIMARY KEY (review_id, user_id),
    constraint "reviews_likes_review_id"
        FOREIGN KEY (review_id)
            REFERENCES reviews (review_id) ON DELETE CASCADE,
    constraint "reviews_likes_user_id"
        FOREIGN KEY (user_id)
            REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events (
    event_id        bigint generated by default as identity primary key,
    user_id         bigint not null,
    time_stamp      bigint not null,
    event_type      varchar,
    event_operation varchar,
    entity_id       bigint not null,
    FOREIGN KEY (USER_ID)
        REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);
create unique index if not exists USER_LOGIN_UINDEX on USERS (login);