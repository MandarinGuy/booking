package org.mandarin.booking.adapter.webapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public record AuthController() {

    @PostMapping("/login")
    public void login() {

    }
}
