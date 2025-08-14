package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.port.MemberRegisterer;
import org.mandarin.booking.adapter.webapi.dto.MemberRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public record MemberController(MemberRegisterer memberRegisterer) {

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid MemberRegisterRequest request){
        var register = memberRegisterer.register(request);
        return ResponseEntity.ok(register);
    }
}
