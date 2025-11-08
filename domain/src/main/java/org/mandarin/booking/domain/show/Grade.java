package org.mandarin.booking.domain.show;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;
import org.mandarin.booking.domain.show.ShowRegisterRequest.GradeRequest;

@Entity
@Getter(value = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PACKAGE)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_grade_show_name", columnNames = {"show_id", "name"}))
public class Grade extends AbstractEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private String name;

    private Integer basePrice;

    private Integer quantity;

    static Grade of(Show show, GradeRequest gradeRequest) {
        return new Grade(show, gradeRequest.name(), gradeRequest.basePrice(), gradeRequest.quantity());
    }

    GradeResponse toResponse() {
        return new GradeResponse(getId(), getName(), getBasePrice(), getQuantity());
    }
}
