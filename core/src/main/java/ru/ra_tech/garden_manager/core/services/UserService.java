package ru.ra_tech.garden_manager.core.services;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ConflictResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.EntityNotFoundResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;

@RequiredArgsConstructor
public class UserService {
    private final static String USER_ENTITY = "User";

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    protected ErrorResponse toServerErrorResponse(AppFailure failure) {
        return new ServerErrorResponse(failure);
    }

    public Either<ErrorResponse, UserData> findUser(long id) {
        return repo.findById(id)
                .mapLeft(this::toServerErrorResponse)
                .flatMap(user -> user.toEither(new EntityNotFoundResponse(USER_ENTITY, id)))
                .map(UserData::of);
    }

    private CreateUserDto toCreateUserDto(CreateUserRequest user) {
        return new CreateUserDto(
                user.login(),
                user.name(),
                user.email(),
                passwordEncoder.encode(user.password())
        );
    }

    private ErrorResponse toConflict(List<String> props) {
        return new ConflictResponse(USER_ENTITY, props);
    }

    public Either<ErrorResponse, UserData> createUser(CreateUserRequest user) {
        return repo.checkDataConflict(user.login(), Option.of(user.email()))
                .mapLeft(this::toServerErrorResponse)
                .flatMap(conflict -> conflict
                        ? Either.left(toConflict(List.of("login", "email")))
                        : Either.right(false)
                ).flatMap(ok ->
                        repo.create(toCreateUserDto(user))
                                .mapLeft(ServerErrorResponse::new)
                )
                .map(UserData::of);
    }

    public Either<ErrorResponse, Boolean> deleteUser(long id) {
        return repo.deleteById(id)
                .mapLeft(this::toServerErrorResponse)
                .flatMap(
                        deleted -> deleted
                                ? Either.right(true)
                                : Either.left(new EntityNotFoundResponse(USER_ENTITY, id))
                );
    }
}
