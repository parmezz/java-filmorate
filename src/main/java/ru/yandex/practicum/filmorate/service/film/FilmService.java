package ru.yandex.practicum.filmorate.service.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                film.addLike(userId);
            } else {
                throw new UserNotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new FilmNotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                film.deleteLike(userId);
            } else {
                throw new UserNotFoundException("Лайк от пользователя c ID=" + userId + " не найден!");
            }
        } else {
            throw new FilmNotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    public List<Film> getPopular(Integer count) {
        List<Film> films = filmStorage.getFilms();
        if (count < 1) {
            new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }
        return films.stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
