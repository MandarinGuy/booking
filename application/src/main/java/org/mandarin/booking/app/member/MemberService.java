package org.mandarin.booking.app.member;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.Member;
import org.mandarin.booking.domain.member.Member.MemberCreateCommand;
import org.mandarin.booking.domain.member.MemberRegisterRequest;
import org.mandarin.booking.domain.member.MemberRegisterResponse;
import org.mandarin.booking.domain.member.SecurePasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegisterer {
    private final MemberRegisterValidator validator;
    private final MemberCommandRepository command;
    private final SecurePasswordEncoder securePasswordEncoder;

    @Override
    public MemberRegisterResponse register(MemberRegisterRequest request) {
        validator.checkDuplicateUserId(request.userId());
        validator.checkDuplicateEmail(request.email());

        var createCommand
                = new MemberCreateCommand(request.nickName(), request.userId(), request.password(), request.email());
        var newMember = Member.create(createCommand, securePasswordEncoder);
        var savedMember = this.command.insert(newMember);
        return MemberRegisterResponse.from(savedMember);
    }
}
