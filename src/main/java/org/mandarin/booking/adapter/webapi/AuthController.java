package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.port.AuthUseCase;
import org.mandarin.booking.domain.member.AuthRequest;
import org.mandarin.booking.domain.member.ReissueRequest;
import org.mandarin.booking.domain.member.TokenHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public record AuthController(AuthUseCase authUsecase) {

    @PostMapping("/login")
    public TokenHolder login(@RequestBody @Valid AuthRequest request) {
        return authUsecase.login(request.userId(), request.password());
    }

    @PostMapping("/reissue")
    public TokenHolder reissue(@RequestBody @Valid ReissueRequest request) {
        return authUsecase.reissue(request.refreshToken());
    }
}
