package org.mandarin.booking.domain.member;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.app.SecurePasswordEncoder;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

    private String nickName;

    private String userId;

    private String passwordHash;

    private String email;

    public static Member create(MemberCreateCommand command,
                                SecurePasswordEncoder securePasswordEncoder) {
        var member = new Member();
        member.nickName = command.nickName();
        member.userId = command.userId();
        member.passwordHash = securePasswordEncoder.encode(command.password());
        member.email = command.email();
        return member;
    }

    public boolean matchesPassword(String rawPassword, SecurePasswordEncoder securePasswordEncoder) {
        return securePasswordEncoder.matches(rawPassword, this.passwordHash);
    }
}
