package org.mandarin.booking.domain.member;

public record MemberCreateCommand(String nickName, String userId, String password, String email){
}
