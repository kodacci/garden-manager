package ru.ra_tech.garden_manager.database.repositories;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import ru.ra_tech.garden_manager.failure.AppFailure;

import static org.jooq.impl.DSL.field;

@RequiredArgsConstructor
public abstract class AbstractRWRepository<I, T, R, S extends Record> implements ReadableRepository<I, Option<R>>,
        WritableRepository<I, T, R> {

    private final DSLContext dsl;
    private final Table<S> table;

    protected abstract AppFailure toFailure(Throwable ex);

    protected DSLContext getContext() {
        return dsl;
    }

    protected Table<S> getTable() {
        return table;
    }

    @Override
    public Either<AppFailure, Boolean> deleteById(I id) {
        return Try.of(
                        () -> getContext().update(getTable())
                                .set(field("deleted"), true)
                                .where(field("id").eq(id).and(field("deleted").eq(false)))
                                .execute()
                )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }

    @Override
    public Either<AppFailure, Boolean> exists(I id) {
        return Try.of(
                        () -> getContext()
                                .selectCount()
                                .from(getTable())
                                .where(field("id").eq(id))
                                .fetchOneInto(Integer.class)
                )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }
}
