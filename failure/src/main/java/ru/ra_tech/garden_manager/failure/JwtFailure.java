package ru.ra_tech.garden_manager.failure;

public class JwtFailure extends AbstractFailure {
    public JwtFailure(String source, Throwable cause) {
        super(source, cause);
    }

    @Override
    public String getCode() {
        return "GM:JWT_FAILURE";
    }

    @Override
    public String getDetail() {
        return String.format("JWT failure: %s", getMessage());
    }
}
