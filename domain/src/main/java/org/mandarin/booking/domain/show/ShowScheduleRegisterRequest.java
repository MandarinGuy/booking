package org.mandarin.booking.domain.show;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ShowScheduleRegisterRequest(
        @NotNull(message = "showId is required")
        Long showId,

        @NotNull(message = "startAt is required")
        LocalDateTime startAt,

        @NotNull(message = "endAt is required")
        LocalDateTime endAt,

        @NotNull(message = "use is required")
        @Valid
        SeatUsageRequest use
) {
    @AssertTrue(message = "The end time must be after the start time")
    private boolean isEndAfterStart() {
        return endAt.isAfter(startAt);
    }

    public record SeatUsageRequest(
            @NotNull(message = "sectionId is required")
            Long sectionId,

            List<Long> excludeSeatIds,

            @NotNull(message = "gradeAssignments are required")
            @NotEmpty(message = "gradeAssignments must not be empty")
            List<@Valid GradeAssignmentRequest> gradeAssignments
    ) {
        @AssertTrue(message = "excludeSeatIds must not contain duplicates")
        public boolean hasUniqueExcludeSeatIds() {
            if (excludeSeatIds.isEmpty()) {
                return true;
            }
            Set<Long> uniqueIds = new HashSet<>(excludeSeatIds);
            return uniqueIds.size() == excludeSeatIds.size();
        }

        @AssertTrue(message = "gradeAssignments gradeIds must not contain duplicates")
        public boolean hasUniqueGradeIds() {
            Set<Long> gradeIds = new HashSet<>();
            return gradeAssignments.stream()
                    .map(GradeAssignmentRequest::gradeId)
                    .allMatch(gradeIds::add);
        }

        @AssertTrue(message = "gradeAssignments seatIds must not contain duplicates across all assignments")
        public boolean hasUniqueSeatIdsInGradeAssignments() {
            Set<Long> allSeatIds = new HashSet<>();
            return gradeAssignments.stream()
                    .flatMap(assignment -> assignment.seatIds().stream())
                    .allMatch(allSeatIds::add);
        }
    }

    public record GradeAssignmentRequest(
            @NotNull(message = "gradeId is required")
            Long gradeId,

            @NotNull(message = "seatIds are required")
            @NotEmpty(message = "seatIds must not be empty")
            List<Long> seatIds
    ) {
    }
}
