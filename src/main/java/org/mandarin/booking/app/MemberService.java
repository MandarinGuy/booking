package org.mandarin.booking.app;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.domain.member.MemberRegisterRequest;
import org.mandarin.booking.domain.member.MemberRegisterResponse;
import org.mandarin.booking.app.port.MemberRegisterer;
import org.mandarin.booking.adapter.persist.MemberCommandRepository;
import org.mandarin.booking.domain.member.Member;
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

        var newMember = Member.create(request, securePasswordEncoder);
        var savedMember = command.insert(newMember);
        return MemberRegisterResponse.from(savedMember);
    }
}
