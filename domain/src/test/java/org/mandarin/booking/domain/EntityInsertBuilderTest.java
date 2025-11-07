package org.mandarin.booking.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.sql.PreparedStatement;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.EntityInsertBuilder.Rec;
import org.mockito.InOrder;

class EntityInsertBuilderTest {

    @Test
    @DisplayName("withForeignKey: named @JoinColumn 사용")
    void foreignKey_usesNamedJoinColumn() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecValue.class, EntFkNamed.class)
                .withForeignKey(7L)
                .compile();
        assertTrue(compiled.sql().startsWith("INSERT INTO t (fk_named"));

        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecValue("a", "b"));
        verify(ps).setObject(1, 7L);
    }

    @Test
    @DisplayName("withForeignKey: 공백 이름은 snake_case + _id로 대체")
    void foreignKey_blankName_fallsBack() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecValue.class, EntFkBlank.class)
                .withForeignKey(9L)
                .compile();
        assertTrue(compiled.sql().contains("parent_field_id"));
        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecValue("a", "b"));
        verify(ps).setObject(1, 9L);
    }

    @Test
    @DisplayName("withForeignKey: @JoinColumn 미존재시 예외")
    void foreignKey_noJoin_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                        .withForeignKey(1L)
                        .compile()
        );
    }

    @Test
    @DisplayName("withForeignKey: @JoinColumn 둘 이상이면 예외")
    void foreignKey_multipleJoin_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                EntityInsertBuilder.forTable("t", RecValue.class, EntMultiJoin.class)
                        .withForeignKey(1L)
                        .compile()
        );
    }

    @Test
    @DisplayName("autoBindAll: 컬렉션/비소유 연관 스킵 + AbstractEntity는 id 매핑")
    void autoBindAll_skipsAndMapsId() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecChild.class, EntFull.class)
                .autoBindAll()
                .compile();
        assertTrue(compiled.sql().contains("child_id"));
        assertTrue(compiled.sql().contains("custom_name"));
        assertFalse(compiled.sql().contains("items"));
        assertFalse(compiled.sql().contains("oo"));

        var child = new MyEntity();
        var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(child, 42L);

        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecChild(child, "nm", List.of("1"), "x"));
        InOrder in = inOrder(ps);
        in.verify(ps).setObject(1, 42L);
        in.verify(ps).setObject(2, "nm");
    }

    @Test
    @DisplayName("bind + mapper + 기본/Column 명 매핑 확인")
    void bind_and_mapper_and_columnResolution() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                .bindAs(RecValue::someField, "someField")
                .bindAs(RecValue::snakeCaseField, "snakeCaseField")
                .compile();
        assertTrue(compiled.sql().contains("custom_col"));
        assertTrue(compiled.sql().contains("snake_case_field"));

        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecValue("v1", "v2"));
        InOrder in = inOrder(ps);
        in.verify(ps).setObject(1, "v1");
        in.verify(ps).setObject(2, "v2");
    }

    @Test
    @DisplayName("autoBindAll: @ManyToMany는 스킵, 다른 필드는 포함")
    void autoBindAll_manyToMany_skipped() {
        var compiled = EntityInsertBuilder.forTable("t", RecWithManyToMany.class, EntWithManyToMany.class)
                .autoBindAll()
                .compile();
        assertTrue(compiled.sql().contains("nm_col"));
        assertFalse(compiled.sql().contains("tags"));
    }

    @Test
    @DisplayName("autoBindAll: @OneToOne + @JoinColumn은 포함")
    void autoBindAll_oneToOne_withJoin_included() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecOO.class, EntOOJoin.class)
                .autoBindAll()
                .compile();
        assertTrue(compiled.sql().contains("oo_id"));

        var other = new Other();
        var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(other, 11L);

        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecOO(other));
        verify(ps).setObject(1, 11L);
    }

    @Test
    @DisplayName("componentName 예외 경로: writeReplace 없는 익명 구현 전달 시 IllegalStateException")
    void componentName_catchPath() {
        Rec<@NonNull RecValue, @NonNull String> bad = new Rec<>() {
            @Override
            public String apply(RecValue rec) {
                return rec.someField();
            }
        };
        assertThrows(IllegalStateException.class, () ->
                EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                        .bindAs(bad, "someField")
        );
    }

    @Test
    @DisplayName("autoBindAll: @OneToOne 이지만 @JoinColumn 없는 필드는 스킵")
    void autoBindAll_oneToOne_withoutJoin_skipped() {
        var compiled = EntityInsertBuilder.forTable("t", RecOO_NoJoin.class, EntOO_NoJoin.class)
                .autoBindAll()
                .compile();
        assertTrue(compiled.sql().contains("nm_col"));
        assertFalse(compiled.sql().contains("oo"));
    }

    @Test
    @DisplayName("resolveColumnNameByField: @JoinColumn name 공백이면 snake_case로 매핑")
    void joinColumn_blank_onField_fallsBackToSnakeCase() throws Exception {
        var compiled = EntityInsertBuilder.forTable("t", RecJoinBlank.class, EntJoinBlankField.class)
                .autoBindAll()
                .compile();
        assertTrue(compiled.sql().contains("child_ref"));

        var child = new MyEntity();
        var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(child, 5L);

        PreparedStatement ps = mock(PreparedStatement.class);
        compiled.binder().bind(ps, new RecJoinBlank(child));
        verify(ps).setObject(1, 5L);
    }

    @Test
    @DisplayName("getRecordComponent 예외 분기: 레코드 접근이 아닌 람다")
    void getRecordComponent_exceptionPath() {
        var compiled = EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                .bindAs(rv -> "CONST", "someField")
                .compile();
        PreparedStatement ps = mock(PreparedStatement.class);
        assertThrows(IllegalStateException.class, () ->
                compiled.binder().bind(ps, new RecValue("v1", "v2"))
        );
    }

    @Test
    @DisplayName("바인딩이 하나도 없으면 컴파일 시 예외")
    void noBindings_compile_throws() {
        assertThrows(IllegalStateException.class, () ->
                EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                        .compile()
        );
    }

    @Test
    @DisplayName("bindAs: 존재하지 않는 필드명 매핑 시 예외")
    void bindAs_unknownField_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                EntityInsertBuilder.forTable("t", RecValue.class, EntNoJoin.class)
                        .bindAs(RecValue::someField, "unknown")
        );
    }

    static class EntFkNamed extends AbstractEntity {
        @JoinColumn(name = "fk_named")
        Long parent;
    }

    static class EntFkBlank extends AbstractEntity {
        @JoinColumn
        Long parentField;
    }

    static class EntNoJoin extends AbstractEntity {
        @Column(name = "custom_col")
        String someField;
        String snakeCaseField;
    }

    static class EntMultiJoin extends AbstractEntity {
        @JoinColumn
        Long a;
        @JoinColumn
        Long b;
    }

    static class MyEntity extends AbstractEntity {
    }

    static class Other extends AbstractEntity {
    }

    static class EntFull extends AbstractEntity {
        @JoinColumn(name = "child_id")
        MyEntity child;
        @Column(name = "custom_name")
        String name;
        @OneToMany
        List<String> items;
        @OneToOne
        Other oo;
        String snakeCaseField;
    }

    record RecValue(String someField, String snakeCaseField) {
    }

    record RecChild(MyEntity child, String name, List<String> items, String extra) {
    }

    static class EntJoinBlankField extends AbstractEntity {
        @JoinColumn(name = "")
        MyEntity childRef;
    }

    record RecJoinBlank(MyEntity childRef) {
    }

    static class EntWithManyToMany extends AbstractEntity {
        @Column(name = "nm_col")
        String name;
        @ManyToMany
        List<String> tags;
    }

    record RecWithManyToMany(String name, List<String> tags) {
    }

    static class EntOOJoin extends AbstractEntity {
        @OneToOne
        @JoinColumn(name = "oo_id")
        Other oo;
    }

    record RecOO(Other oo) {
    }

    static class EntOO_NoJoin extends AbstractEntity {
        @OneToOne
        Other oo;
        @Column(name = "nm_col")
        String name;
    }

    record RecOO_NoJoin(Other oo, String name) {
    }
}
