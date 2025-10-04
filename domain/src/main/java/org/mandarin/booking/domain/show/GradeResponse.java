package org.mandarin.booking.domain.show;

public record GradeResponse(
        Long gradeId,
        String name,
        Integer basePrice,
        Integer quantity) {
}
