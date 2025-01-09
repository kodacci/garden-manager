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
import org.springframework.transaction.PlatformTransactionManager;
import ru.ra_tech.garden_manager.database.ExceptionTranslator;
import ru.ra_tech.garden_manager.database.Transactional;
import ru.ra_tech.garden_manager.database.repositories.api.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepositoryImpl;
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
    public Transactional transactional(PlatformTransactionManager manager) {
        return new Transactional(manager);
    }

    @Bean
    public DSLContext dsl(DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Bean
    public GardenRepository gardenRepository(DSLContext dsl) {
        return new GardenRepositoryImpl(dsl);
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
