package org.mandarin.booking.app;

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

    private static String toSnakeCase(String s) {
        return s
                .replaceAll("([a-z])([A-Z])", "$1_$2") // camelCase → camel_Case
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2") // HTTPServer → HTTP_Server
                .toLowerCase();
    }
}
