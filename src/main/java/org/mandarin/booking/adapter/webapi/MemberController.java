package org.mandarin.booking.adapter.webapi;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public record MemberController(MemberRegisterer memberRegisterer) {

    @PostMapping
    public MemberRegisterResponse register(@RequestBody @Valid MemberRegisterRequest request){
        return memberRegisterer.register(request);
    }
}
