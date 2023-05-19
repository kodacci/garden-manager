package ru.ra_tech.garden_manager.database.repositories;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface WritableRepository<ID, T, R> {
    Either<AppFailure, R> create(T entityDto);
    Either<AppFailure, Boolean> deleteById(ID id);
}
