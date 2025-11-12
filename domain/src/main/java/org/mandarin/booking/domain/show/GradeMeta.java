package org.mandarin.booking.domain.show;

public record GradeMeta(
        Long gradeId,
        String name,
        Integer basePrice
) {
    public static GradeMeta from(Grade grade) {
        return new GradeMeta(grade.getId(), grade.getName(), grade.getBasePrice());
    }
}

