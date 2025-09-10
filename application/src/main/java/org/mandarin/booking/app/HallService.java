package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.HallQueryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HallService {
    private final HallQueryRepository queryRepository;

    @EventListener(HallExistCheckEvent.class)
    public void hallExistCheckHandler(HallExistCheckEvent event) {
        if (queryRepository.existsById(event.getHallId())) {
            event.exist();
        }
    }
}
