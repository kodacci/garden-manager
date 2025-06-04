package ru.ra_tech.garden_manager.failure;

import javax.annotation.Nullable;

public class JwtFailure extends AbstractFailure {
    private static final String CODE = "GM:JWT_FAILURE";
    public JwtFailure(String source, @Nullable Throwable cause) {
        super(source, cause);
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getDetail() {
        return String.format("JWT failure: %s", getMessage());
    }
}
