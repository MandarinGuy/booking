package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleJsonParseError(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED).build();
    }

}
