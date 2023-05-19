package ru.ra_tech.garden_manager.core.persistence;

import lombok.val;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

public class ExceptionTranslator implements ExecuteListener {
    @Override
    public void exception(ExecuteContext context) {
        val dialect = context.configuration().dialect();
        val translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
        val exception = context.sqlException();

        if (exception != null) {
            context.exception(
                    translator.translate("Access database using JOOQ", context.sql(), exception)
            );
        }
    }
}
