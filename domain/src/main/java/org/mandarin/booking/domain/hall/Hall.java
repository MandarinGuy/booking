package org.mandarin.booking.domain.hall;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends AbstractEntity {
    @OneToMany(mappedBy = "hall", cascade = ALL, fetch = LAZY)
    private List<Section> sections = new ArrayList<>();

    @Column(unique = true)
    private String hallName;

    private String registantId;

    public Hall(String hallName, String registantId) {
        this.hallName = hallName;
        this.registantId = registantId;
    }

    public List<Long> getSeatsBySectionIdAndSeatIds(Long sectionId, List<Long> seatIds) {
        return sections.stream()
                .filter(section -> section.getId().equals(sectionId))
                .flatMap(section -> section.getSeats().stream())
                .map(AbstractEntity::getId)
                .filter(seatIds::contains)
                .toList();
    }

    public boolean hasSectionOf(Long sectionId) {
        return sections.stream().anyMatch(section -> section.getId().equals(sectionId));
    }

    public List<SeatInsertRow> extractSeatRows() {
        List<SeatInsertRow> rows = new ArrayList<>();
        for (Section section : getSections()) {
            for (Seat seat : section.getSeats()) {
                rows.add(new SeatInsertRow(section, seat.getRowNumber(), seat.getSeatNumber()));
            }
        }
        return rows;
    }

    public void clearSeats() {
        for (Section section : getSections()) {
            section.getSeats().clear();
        }
    }

    public static Hall create(String name, @Size @Valid List<SectionRegisterRequest> sections, String registantId) {
        var hall = new Hall(name, registantId);
        sections.forEach(req
                -> hall.sections.add(Section.create(req, hall)));
        return hall;
    }

    public record SeatInsertRow(Section section, String rowNumber, String seatNumber) {
    }
}
