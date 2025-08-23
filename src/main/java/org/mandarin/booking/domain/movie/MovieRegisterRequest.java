package org.mandarin.booking.domain.movie;

import java.time.LocalDate;
import java.util.List;

public record MovieRegisterRequest(String title, String director, int runtimeMinutes, String genre,
                                   LocalDate releaseDate,
                                   String rating,
                                   String synopsis, String posterUrl, List<String> casts) {
}
