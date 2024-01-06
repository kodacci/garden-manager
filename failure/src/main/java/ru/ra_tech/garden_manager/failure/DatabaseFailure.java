package ru.ra_tech.garden_manager.failure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
public class DatabaseFailure extends AbstractFailure {
    private final String code;
    private final String detail = "Database access error";

    public DatabaseFailure(DatabaseFailureCode code, @Nullable Throwable cause, String source) {
        super(source, cause);
        this.code = code.toString();
    }

    @RequiredArgsConstructor
    public enum DatabaseFailureCode {
        USER_REPOSITORY_FAILURE("GM:DB:USERS_REPOSITORY_FAILURE"),
        AUTH_USER_REPOSITORY_FAILURE("GM:DB:AUTH_USER_REPOSITORY_FAILURE"),
        GARDEN_REPOSITORY_FAILURE("GM:DB:GARDEN_REPOSITORY_FAILURE"),
        USER_ROLE_REPOSITORY_FAILURE("GM:DB:USER_ROLES_REPOSITORY_FAILURE");

        private final String code;

        @Override
        public String toString() {
            return this.code;
        }
    }
}
