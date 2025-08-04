package org.mandarin.booking.adapter.webapi;

import org.mandarin.booking.domain.MemberRegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public record MemberController() {

    @PostMapping
    public void register(@RequestBody MemberRegisterRequest request){

    }
}
