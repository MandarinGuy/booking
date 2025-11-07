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
import java.lang.reflect.RecordComponent;
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
    private final Class<R> dtoType;
    private final Class<E> entityType;
    private final List<ColBinding<R>> binds = new ArrayList<>();

    private EntityInsertBuilder(String table, Class<R> dtoType, Class<E> entityType) {
        this.table = table;
        this.dtoType = dtoType;
        this.entityType = entityType;
    }

    public EntityInsertBuilder<R, E> withForeignKey(Object value) {
        binds.add(ColBinding.constant(resolveSingleJoinColumnName(entityType), value));
        return this;
    }

    public <V> EntityInsertBuilder<R, E> bindAs(Rec<R, V> recordAccessor, String entityFieldName) {
        String component = componentName(recordAccessor);
        String column = resolveColumnNameByField(entityType, entityFieldName);
        binds.add(ColBinding.of(column, r -> toDbValue(getRecordComponent(r, component))));
        return this;
    }

    public EntityInsertBuilder<R, E> autoBindAll() {
        for (RecordComponent rc : dtoType.getRecordComponents()) {
            String name = rc.getName();
            findField(entityType, name).ifPresent(f -> {
                if (f.isAnnotationPresent(OneToMany.class)
                    || f.isAnnotationPresent(ManyToMany.class)
                    || (f.isAnnotationPresent(OneToOne.class) && !f.isAnnotationPresent(JoinColumn.class))) {
                    return;
                }
                String column = resolveColumnNameByField(entityType, name);
                binds.add(ColBinding.of(column, r -> toDbValue(getRecordComponent(r, name))));
            });
        }
        return this;
    }

    public Compiled<R> compile() {
        if (binds.isEmpty()) {
            throw new IllegalStateException("No columns bound for INSERT");
        }

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner holders = new StringJoiner(", ");
        binds.forEach(b -> {
            cols.add(b.column());
            holders.add("?");
        });

        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + holders + ")";
        Binder<R> binder = (ps, item) -> {
            int idx = 1;
            for (ColBinding<R> b : binds) {
                ps.setObject(idx++, b.extractor().apply(item));
            }
        };
        return new Compiled<>(sql, binder);
    }

    public static <R, E> EntityInsertBuilder<R, E> forTable(String table, Class<R> recordType, Class<E> entityType) {
        return new EntityInsertBuilder<>(table, recordType, entityType);
    }

    private <V> Object getRecordComponent(R r, String component) {
        try {
            return dtoType.getMethod(component).invoke(r);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot access record component: " + component, e);
        }
    }

    private static Object toDbValue(Object v) {
        return (v instanceof AbstractEntity ae) ? ae.getId() : v;
    }

    private static String resolveSingleJoinColumnName(Class<?> entityType) {
        return allFields(entityType).stream()
                .filter(f -> f.isAnnotationPresent(JoinColumn.class))
                .reduce((a, b) -> {
                    throw new IllegalArgumentException("Multiple @JoinColumn present");
                })
                .map(f -> {
                    JoinColumn jc = f.getAnnotation(JoinColumn.class);
                    if (!jc.name().isBlank()) {
                        return jc.name();
                    }
                    return toSnakeCase(f.getName()) + "_id";
                })
                .orElseThrow(() -> new IllegalArgumentException("No @JoinColumn present"));
    }

    private static String resolveColumnNameByField(Class<?> entityType, String fieldName) {
        Field f = findField(entityType, fieldName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity field: " + fieldName));
        if (f.isAnnotationPresent(JoinColumn.class) && !f.getAnnotation(JoinColumn.class).name().isBlank()) {
            return f.getAnnotation(JoinColumn.class).name();
        }
        if (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).name().isBlank()) {
            return f.getAnnotation(Column.class).name();
        }
        return toSnakeCase(fieldName);
    }

    private static Optional<Field> findField(Class<?> type, String name) {
        return allFields(type).stream().filter(f -> f.getName().equals(name)).findFirst();
    }

    private static List<Field> allFields(Class<?> type) {
        List<Field> list = new ArrayList<>();
        for (Class<?> t = type; t != Object.class; t = t.getSuperclass()) {
            list.addAll(Arrays.asList(t.getDeclaredFields()));
        }
        return list;
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

    private record ColBinding<R>(String column, Function<R, Object> extractor) {
        static <R> ColBinding<R> of(String column, Function<R, Object> extractor) {
            return new ColBinding<>(column, extractor);
        }

        static <R> ColBinding<R> constant(String column, Object value) {
            return new ColBinding<>(column, r -> value);
        }
    }
}
