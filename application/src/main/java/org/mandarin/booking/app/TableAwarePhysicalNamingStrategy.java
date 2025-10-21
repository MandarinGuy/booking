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
        var table = CURRENT_TABLE.get();
        var physical = "id".equalsIgnoreCase(logical)
                ? (table == null || table.isBlank() ? "id" : table + "_id")
                : toSnakeCase(logical);
        return Identifier.toIdentifier(physical, name.isQuoted());
    }

    private static String toSnakeCase(String s) {
        if (s.isEmpty()) {
            return s;
        }
        var n = s.length();
        var sb = new StringBuilder(n + 8);
        for (int i = 0; i < n; i++) {
            var c = s.charAt(i);
            if (Character.isUpperCase(c)
                && i > 0
                && (Character.isLowerCase(s.charAt(i - 1)) || (i + 1 < n && Character.isLowerCase(s.charAt(i + 1))))) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
