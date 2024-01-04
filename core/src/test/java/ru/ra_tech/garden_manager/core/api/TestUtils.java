package ru.ra_tech.garden_manager.core.api;

import lombok.val;
import org.jooq.DSLContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;

public class TestUtils {
    private TestUtils() {}

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    static UserDto writeUser(DSLContext dsl, CreateUserDto user) {
        val repo = new UserRepository(dsl);
        val dto = new CreateUserDto(user.login(), user.name(), user.email(), passwordEncoder.encode(user.password()));

        return repo.create(dto).get();
    }
}
