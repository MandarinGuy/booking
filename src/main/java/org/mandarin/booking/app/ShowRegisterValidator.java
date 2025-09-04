package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.ShowQueryRepository;
import org.mandarin.booking.domain.show.ShowException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowRegisterValidator {
    private final ShowQueryRepository queryRepository;

    public void checkDuplicateTitle(String title) {
        if (!queryRepository.existsByName(title)) {
            throw new ShowException("이미 존재하는 공연 이름입니다:" + title);
        }
    }


}
