package org.mandarin.booking.app;

import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.app.persist.ShowCommandRepository;
import org.mandarin.booking.app.port.ShowRegisterer;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowCreateCommand;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowService implements ShowRegisterer {
    private final ShowCommandRepository commandRepository;

    @Override
    public ShowRegisterResponse register(ShowRegisterRequest request) {
        var command = ShowCreateCommand.from(request);
        var show = Show.create(command);
        var saved = commandRepository.insert(show);
        return new ShowRegisterResponse(requireNonNull(saved.getId()));
    }
}

