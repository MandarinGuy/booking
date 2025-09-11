package org.mandarin.booking.webapi;

import jakarta.validation.Valid;
import org.mandarin.booking.app.port.MemberRegisterer;
import org.mandarin.booking.domain.member.MemberRegisterRequest;
import org.mandarin.booking.domain.member.MemberRegisterResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
public record MemberController(MemberRegisterer memberRegisterer) {

    @PostMapping
    public MemberRegisterResponse register(@RequestBody @Valid MemberRegisterRequest request) {
        return memberRegisterer.register(request);
    }
}
