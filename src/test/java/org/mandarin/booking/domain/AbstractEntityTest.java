package org.mandarin.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractEntityTest {

    static class Member extends AbstractEntity { }
    static class Product extends AbstractEntity { }

    private static <T extends AbstractEntity> T withId(T entity, Long id) {
        try {
            Field f = AbstractEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
            return entity;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("equals")
    class EqualsSpec {

        @Test
        @DisplayName("동일 인스턴스는 항상 true")
        void same_instance_true() {
            Member m = new Member();
            assertThat(m.equals(m)).isTrue();
        }

        @Test
        @DisplayName("null 비교는 항상 false")
        void null_is_false() {
            Member m = new Member();
            assertThat(m.equals(null)).isFalse();
        }

        @Test
        @DisplayName("엔티티가 다르면 false")
        void different_entity_false() {
            Member m = withId(new Member(), 1L);
            Product p = withId(new Product(), 1L);
            assertThat(m.equals(p)).isFalse();
        }

        @Test
        @DisplayName("id가 둘 다 null이면 false")
        void both_null_id_false() {
            Member a = new Member();
            Member b = new Member();
            assertThat(a.equals(b)).isFalse();
        }

        @Test
        @DisplayName("같은 클래스 & 같은 id면 true")
        void same_class_same_id_true() {
            Member a = withId(new Member(), 10L);
            Member b = withId(new Member(), 10L);
            assertThat(a.equals(b)).isTrue();
        }

        @Test
        @DisplayName("같은 클래스라도 id 다르면 false")
        void same_class_different_id_false() {
            Member a = withId(new Member(), 10L);
            Member b = withId(new Member(), 11L);
            assertThat(a.equals(b)).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCodeSpec {

        @Test
        @DisplayName("equals가 true면 hashCode도 같다")
        void equals_true_implies_same_hashCode() {
            Member a = withId(new Member(), 7L);
            Member b = withId(new Member(), 7L);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("id가 달라도 같은 클래스면 hashCode는 같을 수 있다")
        void different_id_same_class_hashCode_can_equal() {
            Member a = withId(new Member(), 1L);
            Member b = withId(new Member(), 2L);

            assertThat(a).isNotEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("클래스가 다르면 hashCode가 다를 수 있다")
        void different_class_hashCode_may_differ() {
            Member m = withId(new Member(), 1L);
            Product p = withId(new Product(), 1L);

            assertThat(m).isNotEqualTo(p);
            assertThat(m.hashCode() == p.hashCode()).isIn(true, false);
        }

        @Test
        @DisplayName("컬렉션에 넣으면 해시코드가 다르면 다른 엔티티로 인식")
        void hashCode_Collection() {
            Member a = withId(new Member(), 1L);
            Member b = withId(new Member(), 2L);
            Product c = withId(new Product(), 1L);

            Set<AbstractEntity> set = new HashSet<>();
            set.add(a);
            set.add(b);
            set.add(c);

            assertThat(set).hasSize(3);
        }
    }
}
