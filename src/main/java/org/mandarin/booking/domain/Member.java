package org.mandarin.booking.domain;

import static java.util.Objects.requireNonNull;

public class Member {
    private String nickName;

    private String userId;

    private String passwordHash;

    private String email;

    private Member() {
    }

    public static Member register(MemberRegisterRequest request) {
        var member = new Member();
        member.nickName = requireNonNull(request.nickName(),"NickName cannot be null");
        member.userId = requireNonNull(request.userId(),"UserId cannot be null");
        member.passwordHash = requireNonNull(request.passwordHash(),"PasswordHash cannot be null");
        member.email = requireNonNull(request.email(),"Email cannot be null");
        return member;
    }
}
