package org.mandarin.booking.domain.hall;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Getter(value = PACKAGE)
@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
class Section extends AbstractEntity {
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @OneToMany(mappedBy = "section", cascade = ALL, fetch = LAZY)
    private List<Seat> seats = new ArrayList<>();

    private String name;
}
