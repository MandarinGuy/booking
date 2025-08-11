package org.mandarin.booking.adapter.webapi;

import org.mandarin.booking.domain.Member;

public record MemberRegisterResponse(
        String userId,
        String nickname,
        String email
) {
    public static MemberRegisterResponse from(Member member) {
        return new MemberRegisterResponse(
                member.getUserId(),
                member.getNickName(),
                member.getEmail()
        );
    }
}
