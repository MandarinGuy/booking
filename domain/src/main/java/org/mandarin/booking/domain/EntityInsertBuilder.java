package org.mandarin.booking.domain;

import static org.mandarin.booking.StringFormatterUtils.toSnakeCase;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

public final class EntityInsertBuilder<R, E> {
    private final String table;
    private final Class<R> recordType;
    private final Class<E> entityType;
    private final List<Const> constants = new ArrayList<>();
    private final List<Binding<R>> bindings = new ArrayList<>();

    private EntityInsertBuilder(String table, Class<R> recordType, Class<E> entityType) {
        this.table = table;
        this.recordType = recordType;
        this.entityType = entityType;
        if (!recordType.isRecord()) {
            throw new IllegalArgumentException("recordType must be a record");
        }
    }

    public EntityInsertBuilder<R, E> withForeignKey(Object value) {
        String col = resolveSingleJoinColumnName(entityType);
        this.constants.add(new Const(col, value));
        return this;
    }

    public <V, X> EntityInsertBuilder<R, E> bind(Rec<R, V> recordAccessor, Function<V, X> mapper) {
        String component = componentName(recordAccessor);
        String column = resolveColumnNameByField(entityType, component);
        Function<R, ?> extractor = r -> {
            try {
                @SuppressWarnings("unchecked")
                V v = (V) recordType.getMethod(component).invoke(r);
                return mapper.apply(v);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        };
        bindings.add(new Binding<>(column, extractor));
        return this;
    }

    public <V> EntityInsertBuilder<R, E> bindAs(Rec<R, V> recordAccessor, String entityFieldName) {
        String component = componentName(recordAccessor);
        String column = resolveColumnNameByField(entityType, entityFieldName);

        bindings.add(new Binding<>(column, r -> retrieveEntityIdOrValue(r, component)));
        return this;
    }

    public EntityInsertBuilder<R, E> autoBindAll() {
        for (var rc : recordType.getRecordComponents()) {
            String name = rc.getName();
            var fOpt = findField(entityType, name);
            if (fOpt.isEmpty()) {
                continue;
            }
            var f = fOpt.get();
            if (f.getAnnotation(OneToMany.class) != null || f.getAnnotation(ManyToMany.class) != null) {
                continue;
            }
            if (f.getAnnotation(OneToOne.class) != null && f.getAnnotation(JoinColumn.class) == null) {
                continue;
            }

            String column = resolveColumnNameByField(entityType, name);
            bindings.add(new Binding<>(column, r -> retrieveEntityIdOrValue(r, name)));
        }
        return this;
    }

    public Compiled<R> compile() {
        List<String> columns = new ArrayList<>();
        constants.forEach(c -> columns.add(c.column));
        bindings.forEach(b -> columns.add(b.column));

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner holders = new StringJoiner(", ");
        for (String column : columns) {
            cols.add(column);
            holders.add("?");
        }
        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + holders + ")";

        Binder<R> binder = (ps, item) -> {
            int idx = 1;
            for (Const c : constants) {
                setObject(ps, idx++, c.value);
            }
            for (Binding<R> b : bindings) {
                setObject(ps, idx++, b.extractor.apply(item));
            }
        };
        return new Compiled<>(sql, binder);
    }

    public static <R, E> EntityInsertBuilder<R, E> forTable(String table, Class<R> recordType, Class<E> entityType) {
        return new EntityInsertBuilder<>(table, recordType, entityType);
    }

    private Object retrieveEntityIdOrValue(R r, String component) {
        try {
            Object v = recordType.getMethod(component).invoke(r);
            if (v instanceof AbstractEntity ae) {
                return ae.getId();
            }
            return v;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String resolveSingleJoinColumnName(Class<?> entityType) {
        Field chosen = null;
        for (Field f : allFields(entityType)) {
            if (f.getAnnotation(JoinColumn.class) != null) {
                if (chosen != null) {
                    throw new IllegalArgumentException("Multiple @JoinColumn present");
                }
                chosen = f;
            }
        }
        if (chosen == null) {
            throw new IllegalArgumentException("No @JoinColumn present");
        }
        JoinColumn jc = chosen.getAnnotation(JoinColumn.class);
        if (jc != null && !jc.name().isBlank()) {
            return jc.name();
        }
        return toSnakeCase(chosen.getName()) + "_id";
    }

    private static String resolveColumnNameByField(Class<?> entityType, String fieldName) {
        Field f = findField(entityType, fieldName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity field: " + fieldName));
        JoinColumn jc = f.getAnnotation(JoinColumn.class);
        if (jc != null && !jc.name().isBlank()) {
            return jc.name();
        }
        Column c = f.getAnnotation(Column.class);
        if (c != null && !c.name().isBlank()) {
            return c.name();
        }
        return toSnakeCase(fieldName);
    }

    private static List<Field> allFields(Class<?> type) {
        List<Field> list = new ArrayList<>();
        Class<?> t = type;
        while (t != null && t != Object.class) {
            list.addAll(Arrays.asList(t.getDeclaredFields()));
            t = t.getSuperclass();
        }
        return list;
    }

    private static Optional<Field> findField(Class<?> type, String name) {
        return allFields(type).stream().filter(f -> f.getName().equals(name)).findFirst();
    }

    private static void setObject(PreparedStatement ps, int idx, Object value) throws SQLException {
        switch (value) {
            case String s -> ps.setString(idx, s);
            case Integer i -> ps.setInt(idx, i);
            case Long l -> ps.setLong(idx, l);
            case Boolean b -> ps.setBoolean(idx, b);
            case Double d -> ps.setDouble(idx, d);
            case Float f -> ps.setFloat(idx, f);
            case null, default -> ps.setObject(idx, value);
        }
    }

    private static String componentName(Serializable lambda) {
        try {
            Method m = lambda.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
            return sl.getImplMethodName();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve component name", e);
        }
    }

    @FunctionalInterface
    public interface Rec<R, V> extends Function<R, V>, Serializable {
    }

    @FunctionalInterface
    public interface Binder<R> {
        void bind(PreparedStatement ps, R item) throws SQLException;
    }

    public record Compiled<R>(String sql, Binder<R> binder) {
    }

    private record Const(String column, Object value) {
    }

    private record Binding<R>(String column, Function<R, ?> extractor) {
    }
}
