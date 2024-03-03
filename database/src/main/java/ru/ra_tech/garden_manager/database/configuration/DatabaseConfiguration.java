package ru.ra_tech.garden_manager.database.configuration;

import lombok.val;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import ru.ra_tech.garden_manager.database.ExceptionTranslator;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleRepository;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public ExceptionTranslator exceptionTranslator() {
        return new ExceptionTranslator();
    }

    @Bean
    public DefaultConfiguration configuration(
            DataSourceConnectionProvider connectionProvider,
            ExceptionTranslator exceptionTranslator
    ) {
        val config = new DefaultConfiguration();
        config.set(connectionProvider);
        config.set(new DefaultExecuteListenerProvider(exceptionTranslator));
        config.setSQLDialect(SQLDialect.POSTGRES);

        return config;
    }

    @Bean
    public DSLContext dsl(DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Bean
    public GardenRepository gardenRepository(DSLContext dsl) {
        return new GardenRepository(dsl);
    }

    @Bean
    public UserRepository userRepository(DSLContext dsl) {
        return new UserRepository(dsl);
    }

    @Bean
    public UserRoleRepository userRoleRepository(DSLContext dsl) {
        return new UserRoleRepository(dsl);
    }
}
