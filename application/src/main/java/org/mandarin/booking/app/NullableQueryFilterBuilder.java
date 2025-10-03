package org.mandarin.booking.app;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.TemporalExpression;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * Querydsl BooleanBuilder를 감싸는 유틸리티 빌더 클래스 - 조건을 "값이 존재할 때만" 동적으로 추가할 수 있도록 도와줌 - 가독성을 높이고 null/blank 체크를 내부로 숨겨 호출부를
 * 단순하게 만듦
 */
public class NullableQueryFilterBuilder {
    private final BooleanBuilder builder = new BooleanBuilder();

    private NullableQueryFilterBuilder() {
    }

    public BooleanBuilder build() {
        return builder;
    }

    /**
     * 값이 null이 아닐 경우에만 mapper를 통해 Predicate를 생성 후 and 조건으로 추가 예: when(type, t -> show.type.eq(t))
     */
    public <T> NullableQueryFilterBuilder when(@Nullable T value, Function<T, Predicate> mapper) {
        if (value != null) {
            builder.and(mapper.apply(value));
        }
        return this;
    }

    /**
     * 문자열이 null이 아니고 공백이 아닐 때만 mapper 적용 예: whenHasText(q, s -> show.title.containsIgnoreCase(s))
     */
    public NullableQueryFilterBuilder whenHasText(@Nullable String value, Function<String, Predicate> mapper) {
        if (value != null && !value.isBlank()) {
            builder.and(mapper.apply(value));
        }
        return this;
    }

    /**
     * of, to 둘 다 존재할 때만 between 조건을 추가
     *
     * @param from      요청 구간 시작 (예: LocalDate, LocalDateTime)
     * @param to        요청 구간 종료
     * @param startExpr 엔티티의 시작 값 경로 (예: show.performanceStartDate)
     * @param endExpr   엔티티의 종료 값 경로 (예: show.performanceEndDate)
     */
    public <T extends Comparable<? super T>> NullableQueryFilterBuilder whenInPeriod(
            @Nullable T from,
            @Nullable T to,
            TemporalExpression<T> startExpr,
            TemporalExpression<T> endExpr
    ) {
        if (from != null) {
            builder.and(startExpr.after(from));
        }
        if (to != null) {
            builder.and(endExpr.before(to));
        }
        return this;
    }

    public static NullableQueryFilterBuilder builder() {
        return new NullableQueryFilterBuilder();
    }

//    /**
//     * 두 값이 모두 존재할 때만 mapper 적용
//     * 예: whenBoth(of, to, (f, t) -> show.performanceDate.between(f, t))
//     */
//    public <L, R> NullableQueryFilterBuilder whenBoth(@Nullable L left, @Nullable R right, BiFunction<L, R, Predicate> mapper) {
//        if (left != null && right != null) {
//            builder.and(mapper.apply(left, right));
//        }
//        return this;
//    }
//
//    /**
//     * BooleanExpression을 직접 and 조건으로 추가
//     * (이미 조건식이 준비된 경우 사용)
//     */
//    public NullableQueryFilterBuilder and(@Nullable BooleanExpression expr) {
//        if (expr != null) {
//            builder.and(expr);
//        }
//        return this;
//    }
}
