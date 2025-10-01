package org.mandarin.booking.adapter.webapi;

import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hall")
class HallController {
    @PostMapping
    HallRegisterResponse register(@RequestBody HallRegisterRequest request) {
        return null;
    }
}
