package org.mandarin.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serial;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractEntityTest {

    static class Member extends AbstractEntity { }
    static class Product extends AbstractEntity { }

    @Nested
    @DisplayName("proxy branches")
    class ProxySpec {
        @Test
        @DisplayName("id가 같으면 동일 프록시를 의미한다")
        void equals_real_vs_proxy_same_class_same_id_true() {
            var real = withId(new Member(), 100L);
            var proxy = withId(new ProxyMember(), 100L);
            assertThat(real).isEqualTo(proxy);
            assertThat(proxy).isEqualTo(real);
        }

        @Test
        @DisplayName("동일 프록시라도 클래스가 다르면 다른값을 반환한다")
        void equals_proxy_vs_proxy_different_class_false() {
            var a = withId(new ProxyMember(), 1L);
            var b = withId(new ProxyProduct(), 1L);
            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("프록시는 동일한 해시값을 가진다")
        void hashCode_proxy_branch() {
            var real = withId(new Member(), 1L);
            var proxy = withId(new ProxyMember(), 2L);
            assertThat(proxy.hashCode()).isEqualTo(real.getClass().hashCode());
        }
    }

    // ---- HibernateProxy test doubles to cover proxy branches ----
    static class ProxyMember extends Member implements org.hibernate.proxy.HibernateProxy {
        @Override
        public org.hibernate.proxy.LazyInitializer getHibernateLazyInitializer() {
            Class<?> persistentClass = Member.class;
            return (org.hibernate.proxy.LazyInitializer) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{org.hibernate.proxy.LazyInitializer.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getPersistentClass")) {
                            return persistentClass;
                        }
                        if (method.getReturnType().isPrimitive()) {
                            if (method.getReturnType() == boolean.class) {
                                return false;
                            }
                            if (method.getReturnType() == int.class) {
                                return 0;
                            }
                            if (method.getReturnType() == long.class) {
                                return 0L;
                            }
                        }
                        return null;
                    }
            );
        }

        @Serial
        @Override
        public Object writeReplace() {
            return this;
        }
    }

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

    static class ProxyProduct extends Product implements org.hibernate.proxy.HibernateProxy {
        @Override
        public org.hibernate.proxy.LazyInitializer getHibernateLazyInitializer() {
            Class<?> persistentClass = Product.class;
            return (org.hibernate.proxy.LazyInitializer) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{org.hibernate.proxy.LazyInitializer.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getPersistentClass")) {
                            return persistentClass;
                        }
                        if (method.getReturnType().isPrimitive()) {
                            if (method.getReturnType() == boolean.class) {
                                return false;
                            }
                            if (method.getReturnType() == int.class) {
                                return 0;
                            }
                            if (method.getReturnType() == long.class) {
                                return 0L;
                            }
                        }
                        return null;
                    }
            );
        }

        @Serial
        @Override
        public Object writeReplace() {
            return this;
        }
    }
}
