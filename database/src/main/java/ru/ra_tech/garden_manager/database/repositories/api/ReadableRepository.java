package ru.ra_tech.garden_manager.database.repositories.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface ReadableRepository<I, T> {
    Either<AppFailure, T> findById(I id);
    Either<AppFailure, Boolean> exists(I id);
}
