package ru.ra_tech.garden_manager.failure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

public class DatabaseFailure extends AbstractFailure {
    @Getter
    private final String code;
    @Getter
    private final String detail = "Database access error";

    public DatabaseFailure(DatabaseFailureCode code, @Nullable Throwable cause, String source) {
        super(source, cause);
        this.code = code.toString();
    }

    public DatabaseFailure(@Nullable Throwable cause, String instance) {
        super(instance, cause);
        code = DatabaseFailureCode.UNKNOWN_FAILURE.toString();
    }

    @RequiredArgsConstructor
    public enum DatabaseFailureCode {
        UNKNOWN_FAILURE("GM:DB:UNKNOWN"),
        USER_REPOSITORY_FAILURE("GM:DB:USERS_REPOSITORY_FAILURE"),
        AUTH_USER_REPOSITORY_FAILURE("GM:DB:AUTH_USER_REPOSITORY_FAILURE"),
        GARDEN_REPOSITORY_FAILURE("GM:DB:GARDEN_REPOSITORY_FAILURE"),
        USER_ROLES_REPOSITORY_FAILURE("GM:DB:USER_ROLES_REPOSITORY_FAILURE");

        private final String code;

        @Override
        public String toString() {
            return this.code;
        }
    }
}
