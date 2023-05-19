package ru.ra_tech.garden_manager.database.repositories;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface ReadableRepository<ID, T> {
    Either<AppFailure, T> findById(ID id);
    Either<AppFailure, Boolean> exists(ID id);
}
