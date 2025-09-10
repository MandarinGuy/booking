package org.mandarin.booking.domain.member;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mandarin.booking.domain.AbstractEntity;

@Entity
@Getter
@NoArgsConstructor
public class Member extends AbstractEntity {

    private String nickName;

    private String userId;

    private String passwordHash;

    private String email;

    @Convert(converter = MemberAuthorityConverter.class)
    private List<MemberAuthority> authorities = new ArrayList<>();

    public static Member create(MemberCreateCommand command,
                                SecurePasswordEncoder securePasswordEncoder) {
        var member = new Member();
        member.nickName = command.nickName();
        member.userId = command.userId();
        member.passwordHash = securePasswordEncoder.encode(command.password());
        member.email = command.email();
        member.authorities.add(MemberAuthority.USER);
        return member;
    }

    public String[] getParsedAuthorities() {
        return authorities.stream()
                .map(MemberAuthority::getAuthority)
                .toArray(String[]::new);
    }

    public boolean matchesPassword(String rawPassword, SecurePasswordEncoder securePasswordEncoder) {
        return securePasswordEncoder.matches(rawPassword, this.passwordHash);
    }

    public record MemberCreateCommand(String nickName, String userId, String password, String email){
    }
}
