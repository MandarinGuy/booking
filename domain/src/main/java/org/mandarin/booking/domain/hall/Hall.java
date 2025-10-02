package org.mandarin.booking.domain.hall;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    public static Hall create(String name, String registantId) {
        return new Hall(name, registantId);
    }
}
