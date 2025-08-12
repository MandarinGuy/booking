package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public record AuthController(AuthUseCase authUsecase) {

    @PostMapping("/login")
    public TokenHolder login(@RequestBody @Valid AuthRequest request) {
        return authUsecase.login(request);
    }
}
