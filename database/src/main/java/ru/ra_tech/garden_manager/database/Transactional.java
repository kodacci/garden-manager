package ru.ra_tech.garden_manager.database;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ra_tech.garden_manager.failure.AppFailure;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import java.util.function.Function;

@RequiredArgsConstructor
public class Transactional {
    private final TransactionTemplate trx;

    public Transactional(PlatformTransactionManager manager) {
        trx = new TransactionTemplate(manager);
    }

    private AppFailure toFailure(Throwable cause) {
        return new DatabaseFailure(DatabaseFailure.DatabaseFailureCode.TRANSACTION_FAILURE, cause, getClass().getName());
    }

    public <R, F> Either<F, R> execute(TransactionCallback<Either<F, R>> callback, Function<AppFailure, F> failureMapper) {
        return Try.of(() -> trx.execute(callback))
                .getOrElseGet(throwable -> Either.left(failureMapper.apply(toFailure(throwable))));
    }
}
