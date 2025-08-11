package org.mandarin.booking.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import org.mandarin.booking.adapter.webapi.MemberRegisterRequest;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String nickName;

    private String userId;

    private String passwordHash;

    private String email;

    protected Member() {
    }

    public static Member create(MemberRegisterRequest request, PasswordEncoder passwordEncoder) {
        var member = new Member();
        member.nickName = request.nickName();
        member.userId = request.userId();
        member.passwordHash = passwordEncoder.encode(request.password());
        member.email = request.email();
        return member;
    }

    public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }
}
