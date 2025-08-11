package org.mandarin.booking.domain;

import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.webapi.MemberRegisterer;
import org.mandarin.booking.persist.MemberCommandRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegisterer {
    private final MemberRegisterValidator validator;
    private final MemberCommandRepository command;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(MemberRegisterRequest request) {
        validator.checkDuplicateUserId(request.userId());
        validator.checkDuplicateEmail(request.email());

        var newMember = Member.create(request, passwordEncoder);
        command.insert(newMember);
    }
}
