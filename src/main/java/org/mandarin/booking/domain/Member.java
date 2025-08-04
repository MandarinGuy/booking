package org.mandarin.booking.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String nickName;

    private String userId;

    private String passwordHash;

    private String email;

    protected Member() {
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
