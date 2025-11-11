package org.mandarin.booking.app;


import static org.mandarin.booking.StringFormatterUtils.toSnakeCase;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.jspecify.annotations.Nullable;

public class TableAwarePhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final ThreadLocal<@Nullable String> CURRENT_TABLE = new ThreadLocal<>();

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        var table = toSnakeCase(name.getText());
        CURRENT_TABLE.set(table);
        return Identifier.toIdentifier(table, name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        var logical = name.getText();
        var physical = toSnakeCase(logical);
        return Identifier.toIdentifier(physical, name.isQuoted());
    }
}
