package org.mandarin.booking.domain.member;

public record MemberRegisterResponse(
        String userId,
        String nickName,
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
