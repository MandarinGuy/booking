package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.hall.HallRegisterer;
import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hall")
record HallController(HallRegisterer registerer) {
    @PostMapping
    HallRegisterResponse register(Authentication authentication,
                                  @RequestBody @Valid HallRegisterRequest request) {
        return registerer.register(authentication.getName(), request);
    }
}
